package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
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
            is AddBookAction.UpdateBookDetails -> updateAddBookState(action.state)
            is AddBookAction.ClearBookCover -> clearBookCover()
        }
    }

    private fun saveBook() {
        updateState {
            copy(isBookCoverLoading = true, bookCoverError = null)
        }

        val book = with(state.value) {
            Book(
                name = bookName,
                author = bookAuthor,
                coverPath = bookCoverPath,
                pages = bookPages,
                currentPage = bookCurrentPage
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

    private fun updateAddBookState(newState: AddBookState) {
        updateState { newState }
    }

    private fun clearBookCover() {
        updateState {
            copy(bookCoverPath = null)
        }
    }
}
