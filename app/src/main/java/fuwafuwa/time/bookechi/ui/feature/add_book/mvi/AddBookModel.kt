package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import kotlinx.coroutines.launch

class AddBookModel(
    defaultState: AddBookState,
    private val bookRepository: BookRepository,
) : BaseModel<AddBookState, AddBookAction>(defaultState) {

    override fun onAction(action: AddBookAction) {
        when (action) {
            is AddBookAction.SaveBook -> saveBook()
            is AddBookAction.LoadBookCover -> loadBookCover()
            is AddBookAction.UpdateBookDetails -> updateAddBookState(action.state)
        }
    }

    private fun saveBook() {
        updateState {
            copy(isBookCoverLoading = true, bookCoverError = null)
        }

        val book = with(state.value) {
            Book(
                name = state.value.bookName,
                author = state.value.bookAuthor,
                coverPath = state.value.bookCoverPath,
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

    private fun loadBookCover() {
        // TODO: Implement book cover loading logic
    }

    private fun updateAddBookState(newState: AddBookState) {
        updateState { newState }
    }
}
