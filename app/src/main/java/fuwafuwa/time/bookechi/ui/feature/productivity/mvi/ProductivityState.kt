package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.mvi.api.State

data class ProductivityState(
    val booksRead: Int,
    val pagesRead: Int,
    val dayStreak: Int,
    val averagePages: Float,
    val sessions: List<DailyReadingStats> = emptyList()
) : State
