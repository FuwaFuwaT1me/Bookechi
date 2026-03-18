package fuwafuwa.time.bookechi.ui.feature.library.mvi

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.NavigateToAddBook
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateToBookDetails
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.NavigateToUpdateProgress
import kotlinx.coroutines.launch

class LibraryModel(
    defaultState: LibraryState,
    private val bookRepository: BookRepository
) : BaseModel<LibraryState, LibraryAction>(defaultState) {

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

    override fun onAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.LoadLibrary -> handleLoadLibrary()
            is LibraryAction.RefreshLibrary -> handleRefreshLibrary()
            is LibraryAction.NavigateToAddBook -> sendNavigationEvent(NavigateToAddBook)
            is LibraryAction.NavigateToBookDetails -> sendNavigationEvent(
                NavigateToBookDetails(action.book)
            )
            is LibraryAction.NavigateToUpdateProgress -> sendNavigationEvent(
                NavigateToUpdateProgress(action.book)
            )
            is LibraryAction.EditBook -> {
                updateState {
                    copy(
                        editingBook = action.book
                    )
                }
            }
            is LibraryAction.CancelEditingBook -> {
                updateState {
                    copy(
                        editingBook = null
                    )
                }
            }
        }
    }

    private fun handleLoadLibrary() {
        updateState {
            copy(isLoading = true, error = null)
        }
        updateState {
            copy(isLoading = false)
        }
    }

    private fun handleRefreshLibrary() {
        updateState {
            copy(isLoading = true, error = null)
        }
        updateState {
            copy(isLoading = false)
        }
    }
}
