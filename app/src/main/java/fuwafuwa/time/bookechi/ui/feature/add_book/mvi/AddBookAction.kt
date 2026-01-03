package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface AddBookAction : Action {

    data object SaveBook: AddBookAction

    data class UpdateBookDetails(
        val state: AddBookState
    ) : AddBookAction

    data class LoadBookCover(val uri: Uri?) : AddBookAction

    data object ClearBookCover : AddBookAction
}
