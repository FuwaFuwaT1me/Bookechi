package fuwafuwa.time.bookechi.ui.feature.update_progress.mvi

import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.NavigateToUpdateResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UpdateProgressModel(
    defaultState: UpdateProgressState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<UpdateProgressState, UpdateProgressAction>(defaultState) {

    override fun onAction(action: UpdateProgressAction) {
        when (action) {
            is UpdateProgressAction.NavigateBack -> {
                sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            }
            is UpdateProgressAction.UpdatePageInput -> {
                updateState {
                    copy(updatedInputPages = action.value)
                }
            }
            is UpdateProgressAction.UpdatePageInputByPreset -> {
                updateState {
                    val updatedPages = updatedInputPages + action.value

                    if (updatedPages <= book.pages) {
                        copy(updatedInputPages = updatedPages)
                    } else {
                        copy(updatedInputPages = book.pages)
                    }
                }
            }
            is UpdateProgressAction.UpdateReadingTime -> {
                updateState {
                    copy(readingTimeMinutes = action.value.coerceAtLeast(0))
                }
            }
            is UpdateProgressAction.OpenReadingTimeSheet -> {
                updateState { copy(isReadingTimeSheetOpen = true) }
            }
            is UpdateProgressAction.CloseReadingTimeSheet -> {
                updateState { copy(isReadingTimeSheetOpen = false) }
            }
            is UpdateProgressAction.SaveChanges -> scope.launch {
                val state = currentState()
                // Дошли до конца книги — автоматически помечаем «Прочитано».
                val reachedEnd = state.book.pages > 0 && action.value >= state.book.pages
                val updatedBook = state.book.copy(
                    currentPage = action.value,
                    readingStatus = if (reachedEnd) ReadingStatus.Completed else ReadingStatus.Reading
                )

                // Был ли сегодня стрик-день ДО записи: если нет — этой записью
                // серия продлевается (показываем перебивку один раз за день).
                val wasStreakToday = readingSessionRepository
                    .getCurrentStreak()
                    .first()
                    .isTodayStreak

                bookRepository.updateBook(updatedBook)
                readingSessionRepository.recordReadingProgress(
                    bookId = updatedBook.id,
                    startPage = state.startPages,
                    endPage = action.value,
                    readingTimeMinutes = state.readingTimeMinutes
                )

                sendNavigationEvent(
                    NavigateToUpdateResult(
                        startPages = state.startPages,
                        updatedPages = action.value,
                        allBookPages = updatedBook.pages,
                        bookId = updatedBook.id,
                        streakExtended = !wasStreakToday,
                        readingTimeMinutes = state.readingTimeMinutes,
                        bookName = updatedBook.name,
                        bookAuthor = updatedBook.author,
                        coverPath = updatedBook.coverPath.orEmpty(),
                    )
                )
            }
        }
    }
}
