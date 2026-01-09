package fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface ReadingGoalsAction : Action {
    data object LoadGoals : ReadingGoalsAction
    data object RefreshGoals : ReadingGoalsAction
    
    data class UpdateDailyGoal(val pages: Int) : ReadingGoalsAction
    data class UpdateWeeklyGoal(val books: Int) : ReadingGoalsAction
    data class UpdateYearlyGoal(val books: Int) : ReadingGoalsAction
    
    data class SetEditingDaily(val editing: Boolean) : ReadingGoalsAction
    data class SetEditingWeekly(val editing: Boolean) : ReadingGoalsAction
    data class SetEditingYearly(val editing: Boolean) : ReadingGoalsAction
}

