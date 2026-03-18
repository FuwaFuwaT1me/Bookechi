package fuwafuwa.time.bookechi.ui.feature.update_progress.mvi

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.NavigateToUpdateResult
import kotlinx.coroutines.launch

class UpdateProgressModel(
    defaultState: UpdateProgressState,
    private val bookRepository: BookRepository,
) : BaseModel<UpdateProgressState, UpdateProgressAction>(defaultState) {

    override fun onAction(action: UpdateProgressAction) {
        when (action) {
            is UpdateProgressAction.NavigateBack -> {
                sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            }
            is UpdateProgressAction.UpdatePageInput -> {
                updateState {
                    copy(updatedInputPages = action.value)
                }
            }
            is UpdateProgressAction.UpdatePageInputByPreset -> {
                updateState {
                    val updatedPages = updatedInputPages + action.value

                    if (updatedPages <= book.pages) {
                        copy(updatedInputPages = updatedPages)
                    } else {
                        copy(updatedInputPages = book.pages)
                    }
                }
            }
            is UpdateProgressAction.SaveChanges -> scope.launch {
                val state = currentState()
                val updatedBook = state.book.copy(currentPage = action.value)

                bookRepository.updateBook(updatedBook)
                sendNavigationEvent(
                    NavigateToUpdateResult(
                        startPages = state.startPages,
                        updatedPages = action.value,
                        allBookPages = updatedBook.pages
                    )
                )
            }
        }
    }
}
