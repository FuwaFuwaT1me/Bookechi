package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseModel

class BookListModel(
    defaultState: BookListState
) : BaseModel<BookListState, BookListAction>(defaultState) {

    override fun onAction(action: BookListAction) {
        when (action) {
            is BookListAction.LoadBooks -> handleLoadBooks()
            is BookListAction.RefreshBooks -> handleRefreshBooks()
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
}
