package fuwafuwa.time.bookechi.ui.feature.library.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface LibraryAction : Action {
    data object LoadLibrary : LibraryAction
    data object RefreshLibrary : LibraryAction

    data object OpenAddBookSheet : LibraryAction
    data object SaveAddingBook : LibraryAction
    data object CancelAddingBook : LibraryAction
    data class UpdateAddingBookName(val name: String) : LibraryAction
    data class UpdateAddingBookAuthor(val author: String) : LibraryAction
    data class UpdateAddingBookCurrentPage(val page: Int) : LibraryAction
    data class UpdateAddingBookAllPages(val pages: Int) : LibraryAction
    data class UpdateAddingBookReadingNow(val readingNow: Boolean) : LibraryAction
    data class LoadAddingBookCover(val uri: Uri?) : LibraryAction
    data object ClearAddingBookCover : LibraryAction

    data class EditBook(val book: Book) : LibraryAction
    data class UpdateBook(val book: Book) : LibraryAction
    data object CancelEditingBook : LibraryAction

    data class NavigateToBookDetails(val book: Book) : LibraryAction
    data class NavigateToUpdateProgress(val book: Book) : LibraryAction
}
