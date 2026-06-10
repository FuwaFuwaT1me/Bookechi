package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.NavigateToAddBook
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateToBookDetails
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.NavigateToSettings
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.NavigateToUpdateProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class BookListModel(
    defaultState: BookListState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository,
    private val appPreferences: AppPreferences,
) : BaseModel<BookListState, BookListAction>(defaultState) {

    init {
        scope.launch {
            appPreferences.reminderEnabled.collect { enabled ->
                updateState { copy(reminderEnabled = enabled) }
            }
        }
        scope.launch {
            appPreferences.reminderTime.collect { time ->
                updateState { copy(reminderTime = time) }
            }
        }

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
                .getWeeklyStreak()
                .collect { streak ->
                    updateState {
                        copy(
                            weekDayStreaks = streak.days.map { day ->
                                DayStreak(
                                    isStreakDay = day.isStreakDay,
                                    isToday = day.isToday
                                )
                            },
                            totalDaysWithStreak = streak.totalDays,
                            isTodayStreak = streak.isTodayStreak
                        )
                    }
                }
        }

        scope.launch {
            readingSessionRepository
                .getDailyStatsForCurrentWeek()
                .collect { stats ->
                    val weeklyPages = stats.sumOf { it.totalPagesRead }
                    val today = LocalDate.now().toString()
                    val todayPages = stats
                        .firstOrNull { it.date == today }
                        ?.totalPagesRead
                        ?: 0
                    updateState {
                        copy(
                            weeklyPagesRead = weeklyPages,
                            pagesReadToday = todayPages
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
            is BookListAction.OpenSettings -> sendNavigationEvent(NavigateToSettings)
            is BookListAction.OpenReminderSheet -> updateState { copy(showReminderSheet = true) }
            is BookListAction.CloseReminderSheet -> updateState { copy(showReminderSheet = false) }
            is BookListAction.SetReminderEnabled -> appPreferences.setReminderEnabled(action.enabled)
            is BookListAction.SetReminderTime -> appPreferences.setReminderTime(action.time)
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
}
