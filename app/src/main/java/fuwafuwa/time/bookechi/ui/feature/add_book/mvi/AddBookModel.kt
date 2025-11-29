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
            is AddBookAction.SaveBook -> saveBook(action)
            is AddBookAction.LoadBookCover -> loadBookCover()
        }
    }

    private fun saveBook(action: AddBookAction.SaveBook) {
        updateState {
            copy(isBookCoverLoading = true, bookCoverError = null)
        }

        val book = with(state.value) {
            Book(
                name = action.bookName,
                author = action.bookAuthor,
                coverPath = action.bookCoverPath,
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
}
