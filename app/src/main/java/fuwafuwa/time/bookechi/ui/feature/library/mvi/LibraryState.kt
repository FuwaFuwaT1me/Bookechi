package fuwafuwa.time.bookechi.ui.feature.library.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

data class LibraryState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : State
