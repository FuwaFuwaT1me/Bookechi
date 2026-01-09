package fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

class ReadingGoalsModel(
    defaultState: ReadingGoalsState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository
) : BaseModel<ReadingGoalsState, ReadingGoalsAction>(defaultState) {

    init {
        loadGoalsProgress()
    }

    override fun onAction(action: ReadingGoalsAction) {
        when (action) {
            is ReadingGoalsAction.LoadGoals -> loadGoalsProgress()
            is ReadingGoalsAction.RefreshGoals -> loadGoalsProgress()
            is ReadingGoalsAction.UpdateDailyGoal -> updateDailyGoal(action.pages)
            is ReadingGoalsAction.UpdateWeeklyGoal -> updateWeeklyGoal(action.books)
            is ReadingGoalsAction.UpdateYearlyGoal -> updateYearlyGoal(action.books)
            is ReadingGoalsAction.SetEditingDaily -> updateState { copy(isEditingDaily = action.editing) }
            is ReadingGoalsAction.SetEditingWeekly -> updateState { copy(isEditingWeekly = action.editing) }
            is ReadingGoalsAction.SetEditingYearly -> updateState { copy(isEditingYearly = action.editing) }
        }
    }

    private fun loadGoalsProgress() {
        scope.launch {
            updateState { copy(isLoading = true) }

            try {
                val today = LocalDate.now()
                
                // Daily progress - pages read today
                val todayPages = readingSessionRepository.getTotalPagesReadForDate(today)
                
                // Get all books to calculate weekly and yearly progress
                val books = bookRepository.getAllBooks().first()
                val completedBooks = books.filter { it.currentPage >= it.pages }
                
                // Yearly progress - books completed this year
                val yearlyCompleted = completedBooks.size // Simplified - in real app would track completion date
                
                // Weekly progress - simplified calculation
                val weeklyCompleted = (yearlyCompleted / 52.0).toInt().coerceAtLeast(0)

                updateState {
                    copy(
                        isLoading = false,
                        currentDailyProgress = todayPages,
                        currentWeeklyProgress = weeklyCompleted,
                        currentYearlyProgress = yearlyCompleted,
                        error = null
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun updateDailyGoal(pages: Int) {
        updateState { 
            copy(
                dailyPagesGoal = pages.coerceAtLeast(1),
                isEditingDaily = false
            ) 
        }
    }

    private fun updateWeeklyGoal(books: Int) {
        updateState { 
            copy(
                weeklyBooksGoal = books.coerceAtLeast(1),
                isEditingWeekly = false
            ) 
        }
    }

    private fun updateYearlyGoal(books: Int) {
        updateState { 
            copy(
                yearlyBooksGoal = books.coerceAtLeast(1),
                isEditingYearly = false
            ) 
        }
    }
}

