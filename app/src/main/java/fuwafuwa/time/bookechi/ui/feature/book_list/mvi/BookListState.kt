package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

data class BookListState(
    val books: List<Book>,
    val isLoading: Boolean = false,
    val error: String? = null,
    val gridColumnCount: Int = 1,
    val streakDays: Int = 0,
    val currentWeekStreakDays: Int = 0,
) : State
