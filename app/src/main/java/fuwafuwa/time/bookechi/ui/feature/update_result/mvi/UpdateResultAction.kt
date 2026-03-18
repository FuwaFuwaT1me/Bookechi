package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface UpdateResultAction : Action {

    data object Done : UpdateResultAction
}
