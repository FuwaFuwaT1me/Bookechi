package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface UpdateResultAction : Action {

    data object Done : UpdateResultAction

    /** «Далее» с финального экрана — сохранить оценку/заметку и уйти на продуктивность. */
    data object FinishContinue : UpdateResultAction

    /** «На полку» с финального экрана — сохранить и открыть полку (под ней продуктивность). */
    data object FinishToShelf : UpdateResultAction

    /** Закрыть полноэкранную перебивку «Серия продлена» → показать результаты. */
    data object DismissStreakIntro : UpdateResultAction

    // TODO: persist rating & note (needs schema) — сейчас только обновляют State фичи.
    data class SetRating(val rating: Int) : UpdateResultAction

    data class SetNote(val note: String) : UpdateResultAction
}
