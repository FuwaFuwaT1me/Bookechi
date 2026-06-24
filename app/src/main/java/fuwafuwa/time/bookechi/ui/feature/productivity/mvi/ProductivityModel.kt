package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.NavigateToReadShelf
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.NavigateToReadingLog
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

class ProductivityModel(
    defaultState: ProductivityState,
    private val readingSessionRepository: ReadingSessionRepository,
    private val bookRepository: BookRepository,
): BaseModel<ProductivityState, ProductivityAction>(defaultState) {

    init {
        scope.launch {
            val currentYear = LocalDate.now().year

            readingSessionRepository
                .getDailyStatsForYear(currentYear)
                .combine(readingSessionRepository.getCurrentStreakDays()) { sessions, streakDays ->
                    sessions to streakDays
                }
                .collect { (sessions, streakDays) ->
                    val daysOfCurrentYear = LocalDate.now().dayOfYear
                    val totalPagesRead = sessions.sumOf { it.totalPagesRead }

                    val spark = sessions
                        .sortedBy { it.date }
                        .takeLast(12)
                        .map { it.totalPagesRead }

                    updateState {
                        copy(
                            averagePages = 1f * totalPagesRead / daysOfCurrentYear,
                            dayStreak = streakDays,
                            pagesRead = totalPagesRead,
                            sessions = sessions,
                            journalSpark = spark,
                        )
                    }
                }
        }

        scope.launch {
            readingSessionRepository.getDailyStatsForCurrentWeek().collect { weekStats ->
                updateState {
                    copy(weeklyPagesRead = weekStats.sumOf { it.totalPagesRead })
                }
            }
        }

        scope.launch {
            bookRepository.getAllBooks()
                .combine(readingSessionRepository.getLastReadDates()) { books, lastRead ->
                    books to lastRead
                }
                .collect { (books, lastRead) ->
                    val completed = books.filter { it.readingStatus == ReadingStatus.Completed }
                    val covers = completed
                        .sortedByDescending { lastRead[it.id] ?: "" }
                        .take(3)
                        .map { ShelfCover(coverPath = it.coverPath, title = it.name, author = it.author) }
                    updateState {
                        copy(
                            booksRead = completed.size,
                            shelfCovers = covers,
                            shelfPagesTotal = completed.sumOf { it.pages },
                        )
                    }
                }
        }

        updateState {
            copy(
                currentYear = LocalDate.now().year,
                currentMonth = LocalDate.now().monthValue
            )
        }
    }

    override fun onAction(action: ProductivityAction) {
        when (action) {
            is ProductivityAction.ToggleActivityChartTab -> {
                updateState {
                    copy(
                        activityChartTab = action.tab
                    )
                }
            }
            ProductivityAction.OpenReadingLog -> {
                sendNavigationEvent(NavigateToReadingLog())
            }
            ProductivityAction.OpenReadShelf -> {
                sendNavigationEvent(NavigateToReadShelf)
            }
            is ProductivityAction.DebugOverwriteYear -> {
                scope.launch {
                    overwriteYearData(
                        year = action.year,
                        pagesPerDay = action.pagesPerDay,
                        booksCount = action.booksCount
                    )
                }
            }
            is ProductivityAction.DebugOverwriteMonth -> {
                scope.launch {
                    overwriteMonthData(
                        year = action.year,
                        month = action.month,
                        pagesPerDay = action.pagesPerDay,
                        booksCount = action.booksCount
                    )
                }
            }
            is ProductivityAction.DebugFillRecentWeeks -> {
                scope.launch {
                    fillRecentWeeks(
                        pagesPerDay = action.pagesPerDay,
                        booksCount = action.booksCount
                    )
                }
            }
            ProductivityAction.DebugClearAll -> {
                scope.launch {
                    clearAllData()
                }
            }
        }
    }

    private suspend fun overwriteYearData(
        year: Int,
        pagesPerDay: Int,
        booksCount: Int
    ) {
        val startDate = LocalDate.of(year, 1, 1)
        val endDate = LocalDate.of(year, 12, 31)
        overwriteData(startDate, endDate, pagesPerDay, booksCount)
    }

    private suspend fun overwriteMonthData(
        year: Int,
        month: Int,
        pagesPerDay: Int,
        booksCount: Int
    ) {
        val safeMonth = month.coerceIn(1, 12)
        val yearMonth = YearMonth.of(year, safeMonth)
        overwriteData(yearMonth.atDay(1), yearMonth.atEndOfMonth(), pagesPerDay, booksCount)
    }

    private suspend fun overwriteData(
        startDate: LocalDate,
        endDate: LocalDate,
        pagesPerDay: Int,
        booksCount: Int
    ) {
        val safeBooksCount = booksCount.coerceAtLeast(1)
        val safePagesPerDay = pagesPerDay.coerceAtLeast(0)

        clearAllData()

        val books = (1..safeBooksCount).map { index ->
            Book(
                id = index.toLong(),
                name = "Debug Book $index",
                author = "Debug",
                coverPath = null,
                pages = 500,
                currentPage = 500,
                readingStatus = ReadingStatus.Completed,
                isFavorite = false,
            )
        }
        bookRepository.insertBooks(books)

        val sessions = buildList {
            var date = startDate
            var dayIndex = 0
            while (!date.isAfter(endDate)) {
                val pages = if ((dayIndex + 1) % 8 == 0) {
                    0
                } else {
                    safePagesPerDay + (dayIndex % 5) * 5
                }

                if (pages > 0) {
                    val bookId = books[dayIndex % books.size].id
                    add(
                        readingSessionRepository.createSession(
                            bookId = bookId,
                            date = date,
                            pagesRead = pages,
                            readingTimeMinutes = pages / 2,
                            startPage = 0,
                            endPage = pages
                        )
                    )
                }

                date = date.plusDays(1)
                dayIndex++
            }
        }
        readingSessionRepository.insertSessions(sessions)
    }

    private suspend fun fillRecentWeeks(
        pagesPerDay: Int,
        booksCount: Int
    ) {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val startOfRange = startOfWeek.minusWeeks(3)
        val endOfPreviousWeek = startOfWeek.minusDays(1)
        val halfWeekDays = 4
        val endOfCurrentHalfWeek = minOf(today, startOfWeek.plusDays(halfWeekDays.toLong() - 1))

        val safeBooksCount = booksCount.coerceAtLeast(1)
        val safePagesPerDay = pagesPerDay.coerceAtLeast(1)

        clearAllData()

        val books = (1..safeBooksCount).map { index ->
            Book(
                id = index.toLong(),
                name = "Debug Book $index",
                author = "Debug",
                coverPath = null,
                pages = 500,
                currentPage = 500,
                readingStatus = ReadingStatus.Completed,
                isFavorite = false,
            )
        }
        bookRepository.insertBooks(books)

        val dates = buildList {
            var date = startOfRange
            while (!date.isAfter(endOfPreviousWeek)) {
                add(date)
                date = date.plusDays(1)
            }

            date = startOfWeek
            while (!date.isAfter(endOfCurrentHalfWeek)) {
                add(date)
                date = date.plusDays(1)
            }
        }

        val sessions = dates.mapIndexed { index, date ->
            val pages = safePagesPerDay + (index % 5) * 5
            val bookId = books[index % books.size].id
            readingSessionRepository.createSession(
                bookId = bookId,
                date = date,
                pagesRead = pages,
                readingTimeMinutes = pages / 2,
                startPage = 0,
                endPage = pages
            )
        }

        readingSessionRepository.insertSessions(sessions)
    }

    private suspend fun clearAllData() {
        readingSessionRepository.deleteAllSessions()
        bookRepository.deleteAllBooks()
    }
}
