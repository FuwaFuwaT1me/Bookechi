package fuwafuwa.time.bookechi.mvi.impl

import fuwafuwa.time.bookechi.mvi.api.NavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.DataBundle
import fuwafuwa.time.bookechi.mvi.ui.Screen

sealed interface BaseNavigationEvent : NavigationEvent {

    interface NavigateTo : BaseNavigationEvent {

        val screen: Screen
    }

    interface NavigateBackTo : BaseNavigationEvent {

        val screen: Screen
    }

    /**
     * Сбросить стек до стартового экрана и выстроить поверх него заданную цепочку
     * экранов. Последний экран в [screens] становится текущим, предыдущие — под ним
     * (туда попадёт пользователь по «назад»).
     */
    interface NavigateToStack : BaseNavigationEvent {

        val screens: List<Screen>
    }

    data object NavigateBack : BaseNavigationEvent
}
