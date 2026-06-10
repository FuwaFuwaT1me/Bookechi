package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.mvi.api.State

data class ProductivityState(
    val booksRead: Int = 0,
    val pagesRead: Int = 0,
    val dayStreak: Int = 0,
    val averagePages: Float = 0f,
    val weeklyPagesRead: Int = 0,
    val weeklyPagesTarget: Int = 400,
    val sessions: List<DailyReadingStats> = emptyList(),
    val activityChartTab: ActivityChartTab = ActivityChartTab.MONTH,
    val currentYear: Int = 0,
    val currentMonth: Int = 0,
) : State {

    /** Истинно, когда ни одной сессии и все агрегаты нулевые — показываем «—». */
    val isEmpty: Boolean
        get() = sessions.isEmpty() &&
            booksRead == 0 &&
            pagesRead == 0 &&
            dayStreak == 0 &&
            averagePages == 0f
}

enum class ActivityChartTab {
    MONTH,
    YEAR;

    companion object {

        fun fromIndex(index: Int) = entries.getOrNull(index)
    }
}
