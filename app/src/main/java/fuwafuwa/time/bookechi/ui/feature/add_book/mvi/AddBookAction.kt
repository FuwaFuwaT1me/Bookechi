package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface AddBookAction : Action {

    data class SaveBook(
        val bookName: String,
        val bookAuthor: String,
        val bookCoverPath: String
    ) : AddBookAction

    data object LoadBookCover : AddBookAction
}
