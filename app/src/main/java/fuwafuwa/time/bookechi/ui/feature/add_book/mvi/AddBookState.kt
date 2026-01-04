package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import fuwafuwa.time.bookechi.mvi.api.State

data class AddBookState(
    val bookName: String,
    val bookAuthor: String,
    val readingNow: Boolean,
    val bookPages: Int,
    val bookCurrentPage: Int,
    val bookCoverPath: String? = null,
    val isBookCoverLoading: Boolean = false,
    val bookCoverError: String? = null
) : State
