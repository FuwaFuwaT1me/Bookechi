package fuwafuwa.time.bookechi.ui.feature.library.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateToBookDetails
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.NavigateToReadShelf
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.NavigateToUpdateProgress
import fuwafuwa.time.bookechi.utils.file.CacheHelper
import kotlinx.coroutines.launch

class LibraryModel(
    defaultState: LibraryState,
    private val bookRepository: BookRepository,
    private val cacheHelper: CacheHelper? = null
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

            is LibraryAction.OpenAddBookSheet -> {
                updateState {
                    copy(addingBook = AddingBookDraft())
                }
            }

            is LibraryAction.CancelAddingBook -> {
                updateState {
                    copy(addingBook = null)
                }
            }

            is LibraryAction.UpdateAddingBookName -> updateAddingDraft {
                copy(name = action.name, nameError = false, error = null)
            }

            is LibraryAction.UpdateAddingBookAuthor -> updateAddingDraft {
                copy(author = action.author, error = null)
            }

            is LibraryAction.UpdateAddingBookCurrentPage -> updateAddingDraft {
                copy(currentPage = action.page.filter { it.isDigit() }, error = null)
            }

            is LibraryAction.UpdateAddingBookAllPages -> updateAddingDraft {
                copy(pages = action.pages.filter { it.isDigit() }, pagesError = false, error = null)
            }

            is LibraryAction.UpdateAddingBookStatus -> updateAddingDraft {
                copy(
                    status = action.status,
                    currentPage = if (action.status == ReadingStatus.Reading) currentPage else "",
                    error = null
                )
            }

            is LibraryAction.LoadAddingBookCover -> loadAddingBookCover(action.uri)
            is LibraryAction.ClearAddingBookCover -> updateAddingDraft { copy(coverPath = null) }
            is LibraryAction.SaveAddingBook -> saveAddingBook()

            is LibraryAction.NavigateToBookDetails -> sendNavigationEvent(
                NavigateToBookDetails(action.book)
            )

            is LibraryAction.NavigateToUpdateProgress -> sendNavigationEvent(
                NavigateToUpdateProgress(action.book)
            )

            is LibraryAction.OpenReadShelf -> sendNavigationEvent(NavigateToReadShelf)

            is LibraryAction.EditBook -> {
                updateState {
                    copy(
                        editingBook = action.book
                    )
                }
            }

            is LibraryAction.UpdateBook -> {
                scope.launch {
                    updateState {
                        copy(
                            editingBook = action.book
                        )
                    }
                    bookRepository.updateBook(action.book)
                }
            }

            is LibraryAction.ToggleFavorite -> {
                scope.launch {
                    bookRepository.updateBook(
                        action.book.copy(isFavorite = !action.book.isFavorite)
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

            is LibraryAction.DeleteBook -> {
                scope.launch {
                    updateState {
                        copy(editingBook = null)
                    }
                    bookRepository.deleteBook(action.book)
                }
            }
        }
    }

    private fun updateAddingDraft(transform: AddingBookDraft.() -> AddingBookDraft) {
        updateState {
            val draft = addingBook ?: return@updateState this
            copy(addingBook = draft.transform())
        }
    }

    private fun saveAddingBook() {
        val draft = state.value.addingBook ?: return

        val nameBlank = draft.name.isBlank()
        val pagesValue = draft.pages.toIntOrNull() ?: 0
        val pagesInvalid = pagesValue <= 0
        if (nameBlank || pagesInvalid) {
            updateAddingDraft {
                copy(nameError = nameBlank, pagesError = pagesInvalid)
            }
            return
        }

        updateAddingDraft {
            copy(isSaving = true, error = null)
        }

        val currentPage = when (draft.status) {
            ReadingStatus.Reading ->
                (draft.currentPage.toIntOrNull() ?: 0).coerceIn(0, pagesValue)
            ReadingStatus.Completed -> pagesValue
            else -> 0
        }

        val book = Book(
            name = draft.name.trim(),
            author = draft.author.trim(),
            coverPath = draft.coverPath,
            pages = pagesValue,
            currentPage = currentPage,
            readingStatus = draft.status,
            isFavorite = false
        )

        scope.launch {
            try {
                bookRepository.insertBook(book)
                updateState {
                    copy(addingBook = null)
                }
            } catch (e: Exception) {
                updateAddingDraft {
                    copy(
                        isSaving = false,
                        error = e.message ?: "Не удалось сохранить книгу"
                    )
                }
            }
        }
    }

    private fun loadAddingBookCover(uri: Uri?) {
        if (uri == null) return

        updateAddingDraft {
            copy(isCoverLoading = true, error = null)
        }

        scope.launch {
            try {
                val cachedFile = cacheHelper?.cacheImage(uri)
                updateAddingDraft {
                    copy(
                        isCoverLoading = false,
                        coverPath = cachedFile?.absolutePath ?: uri.toString()
                    )
                }
            } catch (e: Exception) {
                updateAddingDraft {
                    copy(
                        isCoverLoading = false,
                        error = e.message ?: "Не удалось загрузить обложку"
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
