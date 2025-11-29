package fuwafuwa.time.bookechi.ui.feature.book_list.di

import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListModel
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel
import org.koin.dsl.module

val bookListModule = module {
    factory {
        BookListState(
            books = emptyList(),
            isLoading = false,
            error = null
        )
    }

    factory {
        BookListModel(
            defaultState = get<BookListState>()
        )
    }

    factory {
        BookListViewModel(
            model = get<BookListModel>()
        )
    }
}
