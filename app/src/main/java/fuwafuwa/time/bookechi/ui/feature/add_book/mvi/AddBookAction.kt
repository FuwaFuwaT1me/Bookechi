package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import android.net.Uri
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface AddBookAction : Action {

    data object SaveBook : AddBookAction

    data object ClearBookCover : AddBookAction

    data object NavigateBack : AddBookAction

    data class LoadBookCover(val uri: Uri?) : AddBookAction

    data class UpdateBookName(val name: String) : AddBookAction

    data class UpdateBookAuthor(val author: String) : AddBookAction

    data class UpdateCurrentPage(val page: Int) : AddBookAction

    data class UpdateAllPages(val pages: Int) : AddBookAction

    data class UpdateReadingNow(val readingNow: Boolean) : AddBookAction

    // Шаг поиска книги (мок-данные)
    data class UpdateSearchQuery(val query: String) : AddBookAction

    /** Выбор результата поиска → предзаполняет форму и переключает на режим Form. */
    data class SelectSearchResult(
        val title: String,
        val author: String,
        val pages: Int,
        val hasCover: Boolean,
    ) : AddBookAction

    /** Открыть пустую форму вручную (fallback). */
    data object EnterManually : AddBookAction

    /** Вернуться к шагу поиска из формы. */
    data object BackToSearch : AddBookAction
}
