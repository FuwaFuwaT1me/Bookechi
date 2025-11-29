package fuwafuwa.time.bookechi.mvi.impl

import fuwafuwa.time.bookechi.mvi.api.NavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.DataBundle
import fuwafuwa.time.bookechi.mvi.ui.Screen

sealed interface BaseNavigationEvent : NavigationEvent {

    interface NavigateTo : BaseNavigationEvent {

        val screen: Screen
        val dataBundle: DataBundle
    }

    interface NavigateBackTo : BaseNavigationEvent {

        val route: String
    }

    data object NavigateBack : BaseNavigationEvent
}