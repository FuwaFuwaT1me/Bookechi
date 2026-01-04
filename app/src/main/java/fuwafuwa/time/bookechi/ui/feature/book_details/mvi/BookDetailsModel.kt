package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

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
}
