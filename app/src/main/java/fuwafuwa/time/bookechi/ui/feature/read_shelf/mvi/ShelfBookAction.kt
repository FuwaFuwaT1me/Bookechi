package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface ShelfBookAction : Action {
    data object NavigateBack : ShelfBookAction
    data object OpenBookPage : ShelfBookAction
    data object ToggleFavorite : ShelfBookAction
    data object OpenRatingSheet : ShelfBookAction
    data object CloseRatingSheet : ShelfBookAction
    data class SetRating(val rating: Int) : ShelfBookAction
}
