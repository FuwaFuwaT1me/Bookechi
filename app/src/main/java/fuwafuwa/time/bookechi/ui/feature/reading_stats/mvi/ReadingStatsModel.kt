package fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ReadingStatsModel(
    defaultState: ReadingStatsState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository
) : BaseModel<ReadingStatsState, ReadingStatsAction>(defaultState) {

    init {
        loadAllStats()
    }

    override fun onAction(action: ReadingStatsAction) {
        when (action) {
            is ReadingStatsAction.LoadStats -> loadAllStats()
            is ReadingStatsAction.RefreshStats -> loadAllStats()
            is ReadingStatsAction.SelectPeriod -> handleSelectPeriod(action.period)
        }
    }

    private fun loadAllStats() {
        scope.launch {
            updateState { copy(isLoading = true) }

            try {
                val books = bookRepository.getAllBooks().first()
                val completedBooks = books.filter { it.currentPage >= it.pages }
                val totalPagesRead = books.sumOf { it.currentPage }

                val today = LocalDate.now()
                val currentYear = today.year
                val yearMonth = YearMonth.now()
                
                // Load monthly stats
                val monthStats = readingSessionRepository.getDailyStatsForMonth(
                    yearMonth.year,
                    yearMonth.monthValue
                ).first()

                // Load yearly stats for the activity chart
                val yearStats = readingSessionRepository.getDailyStatsForYear(currentYear).first()
                val yearlyReadingData = yearStats.associate { it.date to it.totalPagesRead }

                val streak = calculateCurrentStreak(monthStats.map { LocalDate.parse(it.date) })
                val avgPages = if (monthStats.isNotEmpty()) {
                    monthStats.sumOf { it.totalPagesRead }.toFloat() / monthStats.size
                } else 0f

                updateState {
                    copy(
                        isLoading = false,
                        totalBooksRead = completedBooks.size,
                        totalPagesRead = totalPagesRead,
                        currentStreak = streak,
                        averagePagesPerDay = avgPages,
                        thisMonthStats = monthStats,
                        currentYear = currentYear,
                        yearlyReadingData = yearlyReadingData,
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

    private fun handleSelectPeriod(period: StatsPeriod) {
        updateState { copy(selectedPeriod = period) }
        loadAllStats()
    }

    private fun calculateCurrentStreak(readingDates: List<LocalDate>): Int {
        if (readingDates.isEmpty()) return 0
        
        val sortedDates = readingDates.sortedDescending()
        val today = LocalDate.now()
        
        var streak = 0
        var expectedDate = today
        
        for (date in sortedDates) {
            if (date == expectedDate || date == expectedDate.minusDays(1)) {
                streak++
                expectedDate = date.minusDays(1)
            } else if (date < expectedDate.minusDays(1)) {
                break
            }
        }
        
        return streak
    }
}
