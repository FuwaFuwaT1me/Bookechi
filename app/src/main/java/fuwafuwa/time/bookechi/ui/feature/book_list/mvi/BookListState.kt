package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

data class BookListState(
    val books: List<Book>,
    val isLoading: Boolean = false,
    val error: String? = null,
    val gridColumnCount: Int = 1,
    val totalDaysWithStreak: Int = 0,
    val weekDayStreaks: List<DayStreak>,
) : State

data class DayStreak(
    val isStreakDay: Boolean,
    val isToday: Boolean,
)
