package fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.mvi.api.State
import java.time.LocalDate

data class ReadingStatsState(
    val isLoading: Boolean = false,
    val totalBooksRead: Int = 0,
    val totalPagesRead: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val averagePagesPerDay: Float = 0f,
    val thisMonthStats: List<DailyReadingStats> = emptyList(),
    val selectedPeriod: StatsPeriod = StatsPeriod.THIS_MONTH,
    val error: String? = null
) : State

enum class StatsPeriod {
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    ALL_TIME
}

