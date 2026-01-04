package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

data class BookDetailsState(
    val book: Book?,
    val isLoading: Boolean = false,
    val error: String? = null
) : State

