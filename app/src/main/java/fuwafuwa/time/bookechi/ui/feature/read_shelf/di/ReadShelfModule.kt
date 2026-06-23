package fuwafuwa.time.bookechi.ui.feature.read_shelf.di

import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfBookModel
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfBookState
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfBookViewModel
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfModel
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfState
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val readShelfModule = module {
    viewModel {
        ShelfViewModel(
            model = ShelfModel(
                defaultState = ShelfState(),
                bookRepository = get(),
                readingSessionRepository = get(),
            )
        )
    }
    viewModel { (bookId: Long) ->
        ShelfBookViewModel(
            model = ShelfBookModel(
                defaultState = ShelfBookState(bookId = bookId),
                bookRepository = get(),
                readingSessionRepository = get(),
            )
        )
    }
}
