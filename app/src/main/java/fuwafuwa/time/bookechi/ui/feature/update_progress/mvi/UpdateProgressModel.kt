package fuwafuwa.time.bookechi.ui.feature.update_progress.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent

class UpdateProgressModel(
    defaultState: UpdateProgressState
) : BaseModel<UpdateProgressState, UpdateProgressAction>(defaultState) {

    override fun onAction(action: UpdateProgressAction) {
        when (action) {
            is UpdateProgressAction.NavigateBack -> {
                sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            }
            is UpdateProgressAction.UpdatePageInput -> {
                val filteredValue = action.value.filter { it.isDigit() }
                updateState {
                    copy(pageInput = filteredValue)
                }
            }
        }
    }
}
