package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import fuwafuwa.time.bookechi.mvi.api.State

data class AddBookState(
    val bookName: String,
    val bookAuthor: String,
    val bookCoverPath: String,
    val bookPages: Int,
    val bookCurrentPage: Int,
    val isBookCoverLoading: Boolean = false,
    val bookCoverError: String? = null
) : State
