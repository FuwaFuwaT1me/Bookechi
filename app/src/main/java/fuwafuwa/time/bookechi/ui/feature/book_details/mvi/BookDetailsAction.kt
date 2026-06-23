package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface BookDetailsAction : Action {
    data object NavigateBack : BookDetailsAction
    data class UpdateCurrentPage(val page: Int) : BookDetailsAction
    data object StartReading : BookDetailsAction
    data object PauseReading : BookDetailsAction
    data object ResumeReading : BookDetailsAction
    data object FinishReading : BookDetailsAction
    data object StartReadingAgain : BookDetailsAction
    data object ToggleFavorite : BookDetailsAction
    data object NavigateToUpdateProgress : BookDetailsAction
    data object NavigateToReadingLog : BookDetailsAction

    // Редактирование метаданных книги (лист).
    data object OpenEdit : BookDetailsAction
    data object CloseEdit : BookDetailsAction

    // Оценка книги.
    data object OpenRatingSheet : BookDetailsAction
    data object CloseRatingSheet : BookDetailsAction
    data class SetRating(val rating: Int) : BookDetailsAction
    data class UpdateBook(val book: Book) : BookDetailsAction
    data object DeleteBook : BookDetailsAction
}
