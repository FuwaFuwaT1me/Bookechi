package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookDetailsModel(
    defaultState: BookDetailsState,
    private val bookRepository: BookRepository,
) : BaseModel<BookDetailsState, BookDetailsAction>(defaultState) {

    override fun onAction(action: BookDetailsAction) {
        when (action) {
            is BookDetailsAction.NavigateBack -> sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            is BookDetailsAction.UpdateCurrentPage -> scope.launch {
                handleUpdateCurrentPage(action.page)
            }
            is BookDetailsAction.StartReading -> scope.launch {
                handleUpdateReadingStatus(ReadingStatus.Reading)
            }
            is BookDetailsAction.PauseReading -> scope.launch {
                handleUpdateReadingStatus(ReadingStatus.Paused)
            }
            is BookDetailsAction.ResumeReading -> scope.launch {
                handleUpdateReadingStatus(ReadingStatus.Reading)
            }
            is BookDetailsAction.FinishReading -> scope.launch {
                handleUpdateReadingStatus(ReadingStatus.Stopped)
            }
            is BookDetailsAction.StartReadingAgain -> scope.launch {
                handleUpdateReadingStatus(ReadingStatus.Reading)
            }
        }
    }

    private suspend fun handleUpdateCurrentPage(page: Int) {
        val book = state.value.book ?: return

        withContext(Dispatchers.IO) {
            bookRepository.updateBook(book.copy(currentPage = page))
        }

        updateState {
            copy(book = book.copy(currentPage = page))
        }
    }

    private suspend fun handleUpdateReadingStatus(status: ReadingStatus) {
        val book = state.value.book ?: return
        val updatedBook = book.copy(readingStatus = status)

        withContext(Dispatchers.IO) {
            bookRepository.updateBook(updatedBook)
        }

        updateState {
            copy(book = updatedBook)
        }
    }
}
