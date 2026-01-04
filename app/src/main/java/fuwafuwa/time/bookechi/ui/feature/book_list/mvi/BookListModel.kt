package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.NavigateToAddBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookListModel(
    defaultState: BookListState,
    private val bookRepository: BookRepository,
) : BaseModel<BookListState, BookListAction>(defaultState) {

    init {
        scope.launch {
            bookRepository.getAllBooks().collect { books ->
                updateState {
                    copy(
                        books = books,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    override fun onAction(action: BookListAction) {
        when (action) {
            is BookListAction.LoadBooks -> handleLoadBooks()
            is BookListAction.RefreshBooks -> handleRefreshBooks()
            is BookListAction.DeleteBook -> scope.launch {
                handleDeleteBook(action.book)
            }
            is BookListAction.NavigateToAddBook -> sendNavigationEvent(NavigateToAddBook())
        }
    }

    private fun handleLoadBooks() {
        updateState {
            copy(isLoading = true, error = null)
        }
        // TODO: Implement book loading logic
        updateState {
            copy(isLoading = false)
        }
    }

    private fun handleRefreshBooks() {
        updateState {
            copy(isLoading = true, error = null)
        }
        // TODO: Implement book refresh logic
        updateState {
            copy(isLoading = false)
        }
    }

    private suspend fun handleDeleteBook(book: Book) {
        updateState {
            copy(isLoading = true, error = null)
        }

        withContext(Dispatchers.IO) {
            bookRepository.deleteBook(book)
        }

        updateState {
            copy(isLoading = false)
        }
    }
}
