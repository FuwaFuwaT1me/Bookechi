package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface ProductivityAction : Action {

    data class ToggleActivityChartTab(val tab: ActivityChartTab) : ProductivityAction
}
