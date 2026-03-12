package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

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

                    updateState {
                        copy(
                            averagePages = 1f * totalPagesRead / daysOfCurrentYear,
                            dayStreak = streakDays,
                            pagesRead = totalPagesRead,
                            sessions = sessions
                        )
                    }
                }
        }

        scope.launch {
            bookRepository.getAllBooks().collect { books ->
                updateState {
                    copy(
                        booksRead = books.filter { it.readingStatus == ReadingStatus.Completed }.size
                    )
                }
            }
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
                readingStatus = ReadingStatus.Completed
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

    private suspend fun clearAllData() {
        readingSessionRepository.deleteAllSessions()
        bookRepository.deleteAllBooks()
    }
}
