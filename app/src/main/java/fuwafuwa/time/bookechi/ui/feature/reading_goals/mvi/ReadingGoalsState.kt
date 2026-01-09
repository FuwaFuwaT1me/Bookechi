package fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi

import fuwafuwa.time.bookechi.mvi.api.State

data class ReadingGoalsState(
    val isLoading: Boolean = false,
    val dailyPagesGoal: Int = 20,
    val weeklyBooksGoal: Int = 1,
    val yearlyBooksGoal: Int = 24,
    val currentDailyProgress: Int = 0,
    val currentWeeklyProgress: Int = 0,
    val currentYearlyProgress: Int = 0,
    val isEditingDaily: Boolean = false,
    val isEditingWeekly: Boolean = false,
    val isEditingYearly: Boolean = false,
    val error: String? = null
) : State {
    val dailyProgressPercent: Float
        get() = if (dailyPagesGoal > 0) (currentDailyProgress.toFloat() / dailyPagesGoal).coerceIn(0f, 1f) else 0f
    
    val weeklyProgressPercent: Float
        get() = if (weeklyBooksGoal > 0) (currentWeeklyProgress.toFloat() / weeklyBooksGoal).coerceIn(0f, 1f) else 0f
    
    val yearlyProgressPercent: Float
        get() = if (yearlyBooksGoal > 0) (currentYearlyProgress.toFloat() / yearlyBooksGoal).coerceIn(0f, 1f) else 0f
}

