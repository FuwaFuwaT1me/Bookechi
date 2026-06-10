package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import fuwafuwa.time.bookechi.mvi.api.State

/** Шаг сценария добавления: поиск книги или ручная/предзаполненная форма. */
enum class AddBookMode {
    Search,
    Form,
}

data class AddBookState(
    val bookName: String,
    val bookAuthor: String,
    val readingNow: Boolean,
    val bookPages: Int,
    val bookCurrentPage: Int,
    val bookCoverPath: String? = null,
    val isBookCoverLoading: Boolean = false,
    val bookCoverError: String? = null,
    val showValidationErrors: Boolean = false,
    val mode: AddBookMode = AddBookMode.Search,
    val searchQuery: String = "",
    // true, когда обложка пришла из результата поиска (мок) — показываем блок «Обложка найдена».
    val coverFromSearch: Boolean = false,
) : State
