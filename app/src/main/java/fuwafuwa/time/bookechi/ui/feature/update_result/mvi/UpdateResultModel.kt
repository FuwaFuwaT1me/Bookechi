package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class UpdateResultModel(
    defaultState: UpdateResultState,
    private val readingSessionRepository: ReadingSessionRepository,
    private val bookRepository: BookRepository,
) : BaseModel<UpdateResultState, UpdateResultAction>(defaultState) {

    init {
        scope.launch {
            readingSessionRepository
                .getWeeklyStreak()
                .collect { streak ->
                    updateState {
                        copy(
                            newStreakCount = streak.totalDays,
                            weekDays = streak.days,
                        )
                    }
                }
        }

        // Сколько книг прочитано в этом году (для плашки «N-я книга в этом году»).
        scope.launch {
            combine(
                bookRepository.getAllBooks(),
                readingSessionRepository.getAllSessions(),
            ) { books, sessions ->
                val year = LocalDate.now().year
                val lastDateByBook = sessions
                    .groupBy { it.bookId }
                    .mapValues { (_, list) -> list.maxByOrNull { it.date }?.date }
                books.count { b ->
                    b.readingStatus == ReadingStatus.Completed &&
                        lastDateByBook[b.id]?.let {
                            runCatching { LocalDate.parse(it).year }.getOrNull() == year
                        } == true
                }
            }.collect { count ->
                updateState { copy(booksThisYear = count) }
            }
        }
    }

    override fun onAction(action: UpdateResultAction) {
        when (action) {
            UpdateResultAction.Done -> scope.launch {
                persistRatingAndNote()
                sendNavigationEvent(NavigateBackToHome)
            }

            UpdateResultAction.FinishContinue -> scope.launch {
                persistRatingAndNote()
                sendNavigationEvent(NavigateToProductivityRoot)
            }

            UpdateResultAction.FinishToShelf -> scope.launch {
                persistRatingAndNote()
                sendNavigationEvent(NavigateToShelfFromFinish)
            }

            UpdateResultAction.DismissStreakIntro -> {
                updateState { copy(showStreakIntro = false) }
            }

            is UpdateResultAction.SetRating -> {
                updateState { copy(rating = action.rating) }
            }

            is UpdateResultAction.SetNote -> {
                updateState { copy(note = action.note) }
            }
        }
    }

    /** На «завершённой» ветке сохраняем оценку и заметку в книгу. */
    private suspend fun persistRatingAndNote() {
        val s = currentState()
        if (s.isFinished && s.bookId > 0) {
            val book = bookRepository.getBookById(s.bookId)
            bookRepository.updateBook(book.copy(rating = s.rating, note = s.note))
        }
    }
}
