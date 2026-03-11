package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.mvi.api.State

data class ProductivityState(
    val booksRead: Int,
    val pagesRead: Int,
    val dayStreak: Int,
    val averagePages: Float,
    val sessions: List<DailyReadingStats> = emptyList(),
    val activityChartTab: ActivityChartTab = ActivityChartTab.MONTH
) : State

enum class ActivityChartTab {
    MONTH,
    YEAR;

    companion object {

        fun fromIndex(index: Int) = entries.getOrNull(index)
    }
}
