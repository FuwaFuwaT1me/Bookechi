package fuwafuwa.time.bookechi.ui.feature.library.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface LibraryAction : Action {
    data object LoadLibrary : LibraryAction
    data object RefreshLibrary : LibraryAction

    data object NavigateToAddBook : LibraryAction
    data class NavigateToBookDetails(val book: Book) : LibraryAction
    data class NavigateToUpdateProgress(val book: Book) : LibraryAction
}
