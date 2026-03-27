package fuwafuwa.time.bookechi.ui.feature.library.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateToBookDetails
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
                copy(name = action.name, error = null)
            }

            is LibraryAction.UpdateAddingBookAuthor -> updateAddingDraft {
                copy(author = action.author, error = null)
            }

            is LibraryAction.UpdateAddingBookCurrentPage -> updateAddingDraft {
                val page = action.page.coerceAtLeast(0)
                val normalized = if (pages > 0) page.coerceAtMost(pages) else page
                copy(currentPage = normalized, error = null)
            }

            is LibraryAction.UpdateAddingBookAllPages -> updateAddingDraft {
                val normalizedPages = action.pages.coerceAtLeast(0)
                val normalizedCurrentPage = if (normalizedPages > 0) {
                    currentPage.coerceAtMost(normalizedPages)
                } else {
                    0
                }
                copy(
                    pages = normalizedPages,
                    currentPage = normalizedCurrentPage,
                    error = null
                )
            }

            is LibraryAction.UpdateAddingBookReadingNow -> updateAddingDraft {
                copy(
                    readingNow = action.readingNow,
                    currentPage = if (action.readingNow) currentPage else 0,
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

            is LibraryAction.CancelEditingBook -> {
                updateState {
                    copy(
                        editingBook = null
                    )
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
        if (draft.name.isBlank()) {
            updateAddingDraft {
                copy(error = "Введите название книги")
            }
            return
        }

        updateAddingDraft {
            copy(isSaving = true, error = null)
        }

        val book = Book(
            name = draft.name.trim(),
            author = draft.author.trim(),
            coverPath = draft.coverPath,
            pages = draft.pages,
            currentPage = draft.currentPage,
            readingStatus = if (draft.readingNow) ReadingStatus.Reading else ReadingStatus.None,
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
