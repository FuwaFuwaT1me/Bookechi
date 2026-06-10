package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookMode
import fuwafuwa.time.bookechi.utils.file.CacheHelper
import kotlinx.coroutines.launch

class AddBookModel(
    defaultState: AddBookState,
    private val bookRepository: BookRepository,
    private val cacheHelper: CacheHelper,
) : BaseModel<AddBookState, AddBookAction>(defaultState) {

    override fun onAction(action: AddBookAction) {
        when (action) {
            is AddBookAction.SaveBook -> saveBook()
            is AddBookAction.LoadBookCover -> loadBookCover(action.uri)
            is AddBookAction.ClearBookCover -> clearBookCover()
            is AddBookAction.NavigateBack -> sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            is AddBookAction.UpdateBookName -> updateState { copy(bookName = action.name) }
            is AddBookAction.UpdateBookAuthor -> updateState { copy(bookAuthor = action.author) }
            is AddBookAction.UpdateCurrentPage -> updateState { copy(bookCurrentPage = action.page) }
            is AddBookAction.UpdateAllPages -> updateState { copy(bookPages = action.pages) }
            is AddBookAction.UpdateReadingNow -> updateState { copy(readingNow = action.readingNow) }
            is AddBookAction.UpdateSearchQuery -> updateState { copy(searchQuery = action.query) }
            is AddBookAction.SelectSearchResult -> selectSearchResult(action)
            is AddBookAction.EnterManually -> enterManually()
            is AddBookAction.BackToSearch -> updateState { copy(mode = AddBookMode.Search) }
        }
    }

    private fun selectSearchResult(action: AddBookAction.SelectSearchResult) {
        updateState {
            copy(
                mode = AddBookMode.Form,
                bookName = action.title,
                bookAuthor = action.author,
                bookPages = action.pages,
                bookCurrentPage = 0,
                readingNow = false,
                // Мок-поиск пока не отдаёт реальную обложку — только флаг «найдена».
                // TODO: replace with Open Library API (загрузить cover по URL).
                coverFromSearch = action.hasCover,
                showValidationErrors = false,
                bookCoverError = null,
            )
        }
    }

    private fun enterManually() {
        updateState {
            copy(
                mode = AddBookMode.Form,
                bookName = "",
                bookAuthor = "",
                bookPages = 0,
                bookCurrentPage = 0,
                readingNow = false,
                bookCoverPath = null,
                coverFromSearch = false,
                showValidationErrors = false,
                bookCoverError = null,
            )
        }
    }

    private fun saveBook() {
        val currentState = state.value
        if (!currentState.isInputValid()) {
            updateState {
                copy(showValidationErrors = true)
            }
            return
        }

        updateState {
            copy(
                isBookCoverLoading = true,
                bookCoverError = null,
                showValidationErrors = false
            )
        }

        val book = with(state.value) {
            Book(
                name = bookName.trim(),
                author = bookAuthor,
                coverPath = bookCoverPath,
                pages = bookPages,
                currentPage = bookCurrentPage,
                readingStatus = if (readingNow) ReadingStatus.Reading else ReadingStatus.None,
                isFavorite = false,
            )
        }

        scope.launch {
            try {
                bookRepository.insertBook(book)
                updateState {
                    copy(isBookCoverLoading = false)
                }
                sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            } catch (e: Exception) {
                updateState {
                    copy(
                        isBookCoverLoading = false,
                        bookCoverError = e.message ?: "Failed to save book"
                    )
                }
            }
        }
    }

    private fun loadBookCover(uri: Uri?) {
        updateState {
            copy(isBookCoverLoading = true, bookCoverError = null)
        }

        scope.launch {
            try {
                requireNotNull(uri)

                val cachedFile = cacheHelper.cacheImage(uri)
                updateState {
                    copy(
                        isBookCoverLoading = false,
                        bookCoverPath = cachedFile?.absolutePath
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isBookCoverLoading = false,
                        bookCoverError = e.message ?: "Failed to load cover"
                    )
                }
            }
        }
    }

    private fun clearBookCover() {
        updateState {
            copy(bookCoverPath = null)
        }
    }
}

private fun AddBookState.isInputValid(): Boolean {
    return bookName.trim().isNotEmpty() && bookPages > 0
}
