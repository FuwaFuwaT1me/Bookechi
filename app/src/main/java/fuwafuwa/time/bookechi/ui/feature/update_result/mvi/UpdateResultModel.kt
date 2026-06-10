package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.launch

class UpdateResultModel(
    defaultState: UpdateResultState,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<UpdateResultState, UpdateResultAction>(defaultState) {

    init {
        scope.launch {
            readingSessionRepository
                .getCurrentStreak()
                .collect { streak ->
                    updateState {
                        copy(
                            newStreakCount = streak.totalDays
                        )
                    }
                }
        }
    }

    override fun onAction(action: UpdateResultAction) {
        when (action) {
            UpdateResultAction.Done -> {
                // TODO: persist rating & note (needs schema) before navigating back.
                sendNavigationEvent(NavigateBackToHome)
            }

            is UpdateResultAction.SetRating -> {
                updateState { copy(rating = action.rating) }
            }

            is UpdateResultAction.SetNote -> {
                updateState { copy(note = action.note) }
            }
        }
    }
}
