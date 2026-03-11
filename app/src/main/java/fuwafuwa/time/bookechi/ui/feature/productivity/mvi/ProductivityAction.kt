package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface ProductivityAction : Action {

    data class ToggleActivityChartTab(val tab: ActivityChartTab) : ProductivityAction

    data class DebugOverwriteYear(
        val year: Int,
        val pagesPerDay: Int,
        val booksCount: Int
    ) : ProductivityAction

    data class DebugOverwriteMonth(
        val year: Int,
        val month: Int,
        val pagesPerDay: Int,
        val booksCount: Int
    ) : ProductivityAction

    data object DebugClearAll : ProductivityAction
}
