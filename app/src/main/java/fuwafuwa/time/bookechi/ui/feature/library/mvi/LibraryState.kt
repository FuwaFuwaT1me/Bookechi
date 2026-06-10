package fuwafuwa.time.bookechi.ui.feature.library.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.mvi.api.State

data class LibraryState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val editingBook: Book? = null,
    val addingBook: AddingBookDraft? = null
) : State

/**
 * Черновик добавляемой книги.
 *
 * Статус выбирается одним из трёх значений (Reading / Planned / Completed) через
 * чипы в боттом-шите; поле «Текущая страница» показывается только для статуса
 * [ReadingStatus.Reading]. [pages]/[currentPage] хранятся строками, чтобы поле
 * могло быть пустым (без навязанного «0»).
 */
data class AddingBookDraft(
    val name: String = "",
    val author: String = "",
    val status: ReadingStatus = ReadingStatus.Planned,
    val pages: String = "",
    val currentPage: String = "",
    val coverPath: String? = null,
    val isCoverLoading: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: Boolean = false,
    val pagesError: Boolean = false,
    val error: String? = null
)
