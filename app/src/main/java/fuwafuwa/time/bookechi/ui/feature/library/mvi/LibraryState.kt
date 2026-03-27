package fuwafuwa.time.bookechi.ui.feature.library.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

data class LibraryState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val editingBook: Book? = null,
    val addingBook: AddingBookDraft? = null
) : State

data class AddingBookDraft(
    val name: String = "",
    val author: String = "",
    val readingNow: Boolean = false,
    val pages: Int = 0,
    val currentPage: Int = 0,
    val coverPath: String? = null,
    val isCoverLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
