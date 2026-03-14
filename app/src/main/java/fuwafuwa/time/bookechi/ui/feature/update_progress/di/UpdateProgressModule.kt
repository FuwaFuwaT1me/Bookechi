package fuwafuwa.time.bookechi.ui.feature.update_progress.di

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressModel
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val updateProgressModule = module {
    viewModel { (book: Book) ->
        val state = UpdateProgressState(
            book = book,
        )
        val model = UpdateProgressModel(
            defaultState = state,
        )
        UpdateProgressViewModel(model = model)
    }
}
