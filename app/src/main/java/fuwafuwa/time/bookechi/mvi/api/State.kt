package fuwafuwa.time.bookechi.mvi.api

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

interface State

interface UiStateFlow<UiState : State> {

    val state: StateFlow<UiState>

    fun updateState(updateState: UiState.() -> UiState): Job
}
