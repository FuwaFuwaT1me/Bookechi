package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.mvi.api.State

data class UpdateResultState(
    val pagesDelta: Int,
    val startPages: Int,
    val updatedPages: Int,
    val allBookPages: Int,
    val newStreakCount: Int,
    // TODO: persist rating & note (needs schema) — пока живут только в State этой фичи.
    val rating: Int = 0,
    val note: String = "",
) : State {

    /** Книга дочитана: текущая страница достигла/превысила объём книги. */
    val isFinished: Boolean
        get() = allBookPages > 0 && updatedPages >= allBookPages
}
