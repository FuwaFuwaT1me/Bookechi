package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.productivity.util.getCurrentStreak
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ProductivityModel(
    defaultState: ProductivityState,
    readingSessionRepository: ReadingSessionRepository,
    bookRepository: BookRepository,
): BaseModel<ProductivityState, ProductivityAction>(defaultState) {

    init {
        scope.launch {
            val currentYear = LocalDateTime.now().year
            val daysOfCurrentYear = LocalDateTime.now().dayOfYear

            readingSessionRepository.getDailyStatsForYear(currentYear).collect { sessions ->
                val totalPagesRead = sessions.sumOf { it.totalPagesRead }

                updateState {
                    copy(
                        averagePages = 1f * totalPagesRead / daysOfCurrentYear,
                        dayStreak = sessions.getCurrentStreak(),
                        pagesRead = totalPagesRead,
                        sessions = sessions
                    )
                }
            }
        }

        scope.launch {
            bookRepository.getAllBooks().collect { books ->
                updateState {
                    copy(
                        booksRead = books.filter { it.readingStatus == ReadingStatus.Completed }.size
                    )
                }
            }
        }
    }

    override fun onAction(action: ProductivityAction) {
        when (action) {
            is ProductivityAction.ToggleActivityChartTab -> {
                updateState {
                    copy(
                        activityChartTab = action.tab
                    )
                }
            }
        }
    }
}
