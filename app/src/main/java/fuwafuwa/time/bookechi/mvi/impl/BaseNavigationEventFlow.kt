package fuwafuwa.time.bookechi.mvi.impl

import fuwafuwa.time.bookechi.mvi.api.NavigationEvent
import fuwafuwa.time.bookechi.mvi.api.NavigationEventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class BaseNavigationEventFlow<NavEvent : NavigationEvent>(
    private val scope: CoroutineScope
) : NavigationEventFlow<NavEvent> {

    private val _navigationEvent = MutableSharedFlow<NavEvent>(replay = 0)
    override val navigationEvent: Flow<NavEvent>
        get() = _navigationEvent

    override fun sendNavigationEvent(event: NavEvent) {
        scope.launch(Dispatchers.Default) {
            _navigationEvent.emit(event)
        }
    }
}