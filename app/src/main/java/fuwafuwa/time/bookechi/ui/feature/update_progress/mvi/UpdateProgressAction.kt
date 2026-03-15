package fuwafuwa.time.bookechi.ui.feature.update_progress.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface UpdateProgressAction : Action {

    data object NavigateBack : UpdateProgressAction

    data class UpdatePageInput(val value: Int) : UpdateProgressAction
    data class UpdatePageInputByPreset(val value: Int) : UpdateProgressAction
}
