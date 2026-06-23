package fuwafuwa.time.bookechi.ui.feature.reading_log.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface ReadingLogAction : Action {
    data object NavigateBack : ReadingLogAction

    /** Удалить текущую сессию: запись стирается, прогресс книги откатывается на её начало. */
    data class DeleteSession(val sessionId: Long) : ReadingLogAction

    /** Сохранить правку текущей сессии: новая конечная страница + минуты. */
    data class SaveSessionEdit(
        val sessionId: Long,
        val toPage: Int,
        val minutes: Int,
    ) : ReadingLogAction
}
