package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface ShelfAction : Action {
    data object NavigateBack : ShelfAction
    data class OpenBook(val bookId: Long) : ShelfAction
}
