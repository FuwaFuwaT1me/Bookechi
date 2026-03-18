package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.mvi.api.State

data class UpdateResultState(
    val pagesDelta: Int,
    val startPages: Int,
    val updatedPages: Int,
    val allBookPages: Int,
    val newStreakCount: Int,
) : State
