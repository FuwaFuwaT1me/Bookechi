package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.NavigateToAddBook
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateToBookDetails
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.NavigateToUpdateProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class BookListModel(
    defaultState: BookListState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<BookListState, BookListAction>(defaultState) {

    init {
        scope.launch {
            bookRepository.getAllBooks().collect { books ->
                updateState {
                    copy(
                        books = books,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }

        scope.launch {
            readingSessionRepository
                .getDailyStatsForCurrentWeek()
                .combine(readingSessionRepository.getCurrentStreakDaysBesidesToday()) { weekSessions, streakDays ->
                    weekSessions to streakDays
                }
                .collect { (weekSessions, streakDays) ->
                    val today = LocalDate.now()
                    val hasSessionToday = weekSessions.any { session ->
                        session.localDate == today && session.totalPagesRead > 0
                    }
                    updateState {
                        copy(
                            weekDayStreaks = buildWeekDayStreaks(
                                weekSessions = weekSessions,
                                today = today,
                                streakDays = streakDays + if (hasSessionToday) 1 else 0,
                                hasSessionToday = hasSessionToday
                            ),
                            totalDaysWithStreak = streakDays + if (hasSessionToday) 1 else 0,
                            isTodayStreak = hasSessionToday
                        )
                    }
                }
        }
    }

    override fun onAction(action: BookListAction) {
        when (action) {
            is BookListAction.LoadBooks -> handleLoadBooks()
            is BookListAction.RefreshBooks -> handleRefreshBooks()
            is BookListAction.DeleteBook -> scope.launch {
                handleDeleteBook(action.book)
            }
            is BookListAction.NavigateToEditBook -> sendNavigationEvent(
                NavigateToUpdateProgress(action.book)
            )
            is BookListAction.NavigateToAddBook -> sendNavigationEvent(NavigateToAddBook)
            is BookListAction.NavigateToBookDetails -> sendNavigationEvent(
                NavigateToBookDetails(action.book)
            )
        }
    }

    private fun handleLoadBooks() {
        updateState {
            copy(isLoading = true, error = null)
        }
        // TODO: Implement book loading logic
        updateState {
            copy(isLoading = false)
        }
    }

    private fun handleRefreshBooks() {
        updateState {
            copy(isLoading = true, error = null)
        }
        // TODO: Implement book refresh logic
        updateState {
            copy(isLoading = false)
        }
    }

    private suspend fun handleDeleteBook(book: Book) {
        updateState {
            copy(isLoading = true, error = null)
        }

        withContext(Dispatchers.IO) {
            bookRepository.deleteBook(book)
        }

        updateState {
            copy(isLoading = false)
        }
    }

    private fun buildWeekDayStreaks(
        weekSessions: List<fuwafuwa.time.bookechi.data.model.DailyReadingStats>,
        today: LocalDate,
        streakDays: Int,
        hasSessionToday: Boolean,
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
    ): List<DayStreak> {
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        val sessionsByDate = weekSessions.associateBy { it.localDate }
        val streakEnd = when {
            streakDays <= 0 -> null
            hasSessionToday -> today
            else -> today.minusDays(1)
        }
        val streakStart = streakEnd?.minusDays(streakDays.toLong() - 1)

        return (0..6).map { offset ->
            val date = startOfWeek.plusDays(offset.toLong())
            val session = sessionsByDate[date]

            DayStreak(
                isStreakDay = session != null &&
                    streakStart != null &&
                    streakEnd != null &&
                    !date.isAfter(streakEnd) &&
                    !date.isBefore(streakStart),
                isToday = date == today
            )
        }
    }
}
