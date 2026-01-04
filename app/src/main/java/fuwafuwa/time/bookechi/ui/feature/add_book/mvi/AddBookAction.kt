package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface AddBookAction : Action {

    data object SaveBook : AddBookAction

    data object ClearBookCover : AddBookAction

    data object NavigateBack : AddBookAction

    data class LoadBookCover(val uri: Uri?) : AddBookAction

    data class UpdateBookName(val name: String) : AddBookAction

    data class UpdateBookAuthor(val author: String) : AddBookAction

    data class UpdateCurrentPage(val page: Int) : AddBookAction

    data class UpdateAllPages(val pages: Int) : AddBookAction

    data class UpdateReadingNow(val readingNow: Boolean) : AddBookAction
}
