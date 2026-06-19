package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.NavigateToUpdateProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookDetailsModel(
    defaultState: BookDetailsState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<BookDetailsState, BookDetailsAction>(defaultState) {

    init {
        loadRecentSessions()
    }

    override fun onAction(action: BookDetailsAction) {
        when (action) {
            is BookDetailsAction.NavigateBack -> sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            is BookDetailsAction.NavigateToUpdateProgress -> {
                val book = state.value.book ?: return
                sendNavigationEvent(NavigateToUpdateProgress(book))
            }
            is BookDetailsAction.ToggleFavorite -> scope.launch {
                handleToggleFavorite()
            }
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
                handleUpdateReadingStatus(ReadingStatus.Dropped)
            }
            is BookDetailsAction.StartReadingAgain -> scope.launch {
                handleUpdateReadingStatus(ReadingStatus.Reading)
            }
            is BookDetailsAction.OpenEdit -> updateState { copy(isEditing = true) }
            is BookDetailsAction.CloseEdit -> updateState { copy(isEditing = false) }
            is BookDetailsAction.UpdateBook -> scope.launch {
                handleUpdateBook(action.book)
            }
            is BookDetailsAction.DeleteBook -> scope.launch {
                handleDeleteBook()
            }
        }
    }

    private suspend fun handleUpdateBook(book: Book) {
        withContext(Dispatchers.IO) {
            bookRepository.updateBook(book)
        }
        updateState { copy(book = book) }
    }

    private suspend fun handleDeleteBook() {
        val book = state.value.book ?: return
        withContext(Dispatchers.IO) {
            bookRepository.deleteBook(book)
        }
        updateState { copy(isEditing = false) }
        sendNavigationEvent(BaseNavigationEvent.NavigateBack)
    }

    /**
     * Загружает страницы за последние ~10 дней по книге из сессий чтения.
     * Сессии приходят отсортированными по дате DESC — берём первые 10 и
     * разворачиваем в хронологический порядок (старые → новые) для спарклайна.
     */
    private fun loadRecentSessions() {
        val book = state.value.book ?: return
        scope.launch {
            val pages = withContext(Dispatchers.IO) {
                readingSessionRepository.getSessionsForBook(book.id)
                    .first()
                    .take(RECENT_SESSIONS_LIMIT)
                    .map { it.pagesRead }
                    .reversed()
            }
            updateState { copy(recentSessionPages = pages) }
        }
    }

    private suspend fun handleToggleFavorite() {
        val book = state.value.book ?: return
        val updatedBook = book.copy(isFavorite = !book.isFavorite)

        withContext(Dispatchers.IO) {
            bookRepository.updateBook(updatedBook)
        }

        updateState {
            copy(book = updatedBook)
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

    private companion object {
        const val RECENT_SESSIONS_LIMIT = 10
    }
}
