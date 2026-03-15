package fuwafuwa.time.bookechi.ui.feature.update_progress.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

data class UpdateProgressState(
    val book: Book,
    val startPages: Int = 0,
    val updatedInputPages: Int = 0,
    val isSaving: Boolean = false,
    val error: String? = null
) : State
