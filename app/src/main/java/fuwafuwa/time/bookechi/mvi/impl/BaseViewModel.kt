package fuwafuwa.time.bookechi.mvi.impl

import androidx.lifecycle.ViewModel
import fuwafuwa.time.bookechi.mvi.api.Action
import fuwafuwa.time.bookechi.mvi.api.ActionFlow
import fuwafuwa.time.bookechi.mvi.api.Model
import fuwafuwa.time.bookechi.mvi.api.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseViewModel<UiAction, UiState> : ViewModel()
    where UiAction : Action,
        UiState : State {

    abstract val model: Model<UiState, UiAction, BaseNavigationEvent>

    private val scope = CoroutineScope(Dispatchers.Default)

    private val actionFlow: ActionFlow<UiAction> = BaseActionFlow(scope)
    private val uiActions: Flow<UiAction> = actionFlow.actions

    fun init() {
        scope.coroutineContext.cancelChildren()
        setupCollecting()
    }

    fun sendAction(action: UiAction) {
        scope.launch {
            actionFlow.sendAction(action)
        }
    }

    fun sendNavigationEvent(navEvent: BaseNavigationEvent) {
        model.sendNavigationEvent(navEvent)
    }

    private fun setupCollecting() {
        scope.launch {
            uiActions.collect { viewAction ->
                model.onAction(viewAction)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}