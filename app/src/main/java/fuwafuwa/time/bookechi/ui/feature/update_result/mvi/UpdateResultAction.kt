package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface UpdateResultAction : Action {

    data object Done : UpdateResultAction

    // TODO: persist rating & note (needs schema) — сейчас только обновляют State фичи.
    data class SetRating(val rating: Int) : UpdateResultAction

    data class SetNote(val note: String) : UpdateResultAction
}
