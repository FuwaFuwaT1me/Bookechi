package fuwafuwa.time.bookechi.ui.feature.library.di

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryModel
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val libraryModule = module {
    viewModelOf(::LibraryViewModel)

    factory {
        LibraryState(
            books = emptyList(),
            isLoading = true,
            error = null
        )
    }

    factory {
        LibraryModel(
            defaultState = get<LibraryState>(),
            bookRepository = get<BookRepository>()
        )
    }
}
