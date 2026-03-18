package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateBackToBookDetails
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class UpdateResultModel(
    defaultState: UpdateResultState,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<UpdateResultState, UpdateResultAction>(defaultState) {

    init {
        scope.launch {
            readingSessionRepository
                .getCurrentStreakDaysBesidesToday()
                .collect { streakDays ->
                    updateState {
                        copy(
                            newStreakCount = streakDays + 1
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
