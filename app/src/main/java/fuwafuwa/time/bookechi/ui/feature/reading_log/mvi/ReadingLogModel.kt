package fuwafuwa.time.bookechi.ui.feature.reading_log.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingSession
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

class ReadingLogModel(
    defaultState: ReadingLogState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<ReadingLogState, ReadingLogAction>(defaultState) {

    private var rawSessions: List<ReadingSession> = emptyList()
    private var books: Map<Long, Book> = emptyMap()

    init {
        scope.launch {
            combine(
                readingSessionRepository.getAllSessions(),
                bookRepository.getAllBooks(),
            ) { sessions, bookList -> sessions to bookList }
                .collect { (sessions, bookList) ->
                    rawSessions = sessions
                    books = bookList.associateBy { it.id }
                    rebuild()
                }
        }
    }

    private fun rebuild() {
        val today = LocalDate.now()
        val bookId = state.value.bookId
        val scoped = bookId > 0

        // Последняя сессия каждой книги — кандидат на метку «текущая».
        val latestByBook = rawSessions
            .groupBy { it.bookId }
            .mapValues { (_, list) -> list.maxByOrNull { it.date } }

        val relevant = if (scoped) rawSessions.filter { it.bookId == bookId } else rawSessions

        val items = relevant.mapNotNull { s ->
            val book = books[s.bookId] ?: return@mapNotNull null
            val date = runCatching { LocalDate.parse(s.date) }.getOrNull() ?: return@mapNotNull null
            val isCurrent = latestByBook[s.bookId]?.id == s.id && s.endPage == book.currentPage
            SessionLogItem(
                sessionId = s.id,
                bookId = s.bookId,
                title = book.name,
                author = book.author,
                coverPath = book.coverPath,
                fromPage = s.startPage,
                toPage = s.endPage,
                totalPages = book.pages,
                minutes = s.readingTimeMinutes,
                date = date,
                dayOffset = ChronoUnit.DAYS.between(date, today).toInt(),
                isCurrent = isCurrent,
            )
        }.sortedByDescending { it.date }

        val title = if (scoped) (books[bookId]?.name ?: "") else ""

        updateState {
            copy(
                items = items,
                isBookScope = scoped,
                bookTitle = title,
                weekSummary = if (scoped) PeriodSummary() else computeWeek(today),
                monthSummary = if (scoped) PeriodSummary() else computeMonth(today),
                allTimeSummary = if (scoped) PeriodSummary() else computeAllTime(),
                isLoading = false,
            )
        }
    }

    private fun parsed(): List<Pair<LocalDate, ReadingSession>> =
        rawSessions.mapNotNull { s ->
            runCatching { LocalDate.parse(s.date) }.getOrNull()?.let { it to s }
        }

    private fun aggregate(from: LocalDate, to: LocalDate): Triple<Int, Int, Int> {
        val inRange = parsed().filter { (d, _) -> !d.isBefore(from) && !d.isAfter(to) }
        return Triple(
            inRange.size,
            inRange.sumOf { it.second.pagesRead },
            inRange.sumOf { it.second.readingTimeMinutes },
        )
    }

    private fun deltaPct(cur: Int, prev: Int): Int? =
        if (prev <= 0) null else ((cur - prev) * 100f / prev).roundToInt()

    private fun computeWeek(today: LocalDate): PeriodSummary {
        val start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val end = start.plusDays(6)
        val (s, p, m) = aggregate(start, end)
        val (_, pPrev, _) = aggregate(start.minusWeeks(1), start.minusDays(1))
        return PeriodSummary(sessions = s, pages = p, minutes = m, deltaPercent = deltaPct(p, pPrev))
    }

    private fun computeMonth(today: LocalDate): PeriodSummary {
        val ym = YearMonth.from(today)
        val (s, p, m) = aggregate(ym.atDay(1), ym.atEndOfMonth())
        val prev = ym.minusMonths(1)
        val (_, pPrev, _) = aggregate(prev.atDay(1), prev.atEndOfMonth())
        return PeriodSummary(sessions = s, pages = p, minutes = m, deltaPercent = deltaPct(p, pPrev))
    }

    private fun computeAllTime(): PeriodSummary {
        val parsed = parsed()
        val earliest = parsed.minByOrNull { it.first }?.first
        val since = earliest?.let {
            "${it.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${it.year}"
        } ?: ""
        return PeriodSummary(
            sessions = parsed.size,
            pages = parsed.sumOf { it.second.pagesRead },
            minutes = parsed.sumOf { it.second.readingTimeMinutes },
            booksCount = parsed.map { it.second.bookId }.distinct().size,
            sinceLabel = since,
        )
    }

    override fun onAction(action: ReadingLogAction) {
        when (action) {
            ReadingLogAction.NavigateBack -> sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            is ReadingLogAction.DeleteSession -> scope.launch { handleDelete(action.sessionId) }
            is ReadingLogAction.SaveSessionEdit -> scope.launch { handleSave(action) }
        }
    }

    private suspend fun handleDelete(sessionId: Long) {
        val s = rawSessions.firstOrNull { it.id == sessionId } ?: return
        val book = books[s.bookId]
        withContext(Dispatchers.IO) {
            // Откат закладки на начало сессии (удаляем только «текущую»).
            if (book != null && book.currentPage == s.endPage) {
                // Если эта сессия дочитала книгу — снимаем статус «Прочитано».
                val rolledBackStatus =
                    if (book.readingStatus == ReadingStatus.Completed && s.startPage < book.pages) {
                        ReadingStatus.Reading
                    } else {
                        book.readingStatus
                    }
                bookRepository.updateBook(
                    book.copy(currentPage = s.startPage, readingStatus = rolledBackStatus)
                )
            }
            readingSessionRepository.deleteSession(s)
        }
    }

    private suspend fun handleSave(action: ReadingLogAction.SaveSessionEdit) {
        val s = rawSessions.firstOrNull { it.id == action.sessionId } ?: return
        val book = books[s.bookId]
        val newTo = action.toPage.coerceAtLeast(s.startPage)
        withContext(Dispatchers.IO) {
            readingSessionRepository.updateSession(
                s.copy(
                    endPage = newTo,
                    pagesRead = newTo - s.startPage,
                    readingTimeMinutes = action.minutes.coerceAtLeast(0),
                )
            )
            if (book != null && book.currentPage == s.endPage) {
                val newStatus = when {
                    book.pages > 0 && newTo >= book.pages -> ReadingStatus.Completed
                    book.readingStatus == ReadingStatus.Completed -> ReadingStatus.Reading
                    else -> book.readingStatus
                }
                bookRepository.updateBook(book.copy(currentPage = newTo, readingStatus = newStatus))
            }
        }
    }
}
