package fuwafuwa.time.bookechi.ui.feature.update_progress.di

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressModel
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val updateProgressModule = module {
    viewModel { (book: Book) ->
        val state = UpdateProgressState(
            book = book,
            startPages = book.currentPage,
            updatedInputPages = book.currentPage,
        )
        val model = UpdateProgressModel(
            defaultState = state,
            bookRepository = get<BookRepository>()
        )
        UpdateProgressViewModel(model = model)
    }
}
