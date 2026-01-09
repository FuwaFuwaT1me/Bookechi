package fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface ReadingStatsAction : Action {
    data object LoadStats : ReadingStatsAction
    data object RefreshStats : ReadingStatsAction
    data class SelectPeriod(val period: StatsPeriod) : ReadingStatsAction
}

