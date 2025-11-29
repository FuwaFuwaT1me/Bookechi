package fuwafuwa.time.bookechi.mvi.api

import kotlinx.coroutines.flow.Flow

interface NavigationEvent

interface NavigationEventFlow<NavEvent : NavigationEvent> {

    val navigationEvent: Flow<NavEvent>

    fun sendNavigationEvent(event: NavEvent)
}
