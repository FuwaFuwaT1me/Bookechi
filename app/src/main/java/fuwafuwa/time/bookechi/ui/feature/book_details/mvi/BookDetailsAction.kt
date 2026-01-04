package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface BookDetailsAction : Action {
    data object NavigateBack : BookDetailsAction
    data class UpdateCurrentPage(val page: Int) : BookDetailsAction
}

