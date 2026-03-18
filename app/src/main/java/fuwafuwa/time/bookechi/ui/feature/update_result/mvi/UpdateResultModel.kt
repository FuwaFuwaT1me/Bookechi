package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateBackToBookDetails
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
                sendNavigationEvent(NavigateBackToBookDetails)
            }
        }
    }
}
