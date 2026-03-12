package fuwafuwa.time.bookechi.ui.feature.book_list.di

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListModel
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val bookListModule = module {
    singleOf(::BookListModel) { bind<BookListModel>() }

    viewModelOf(::BookListViewModel)

    factory {
        BookListState(
            books = emptyList(),
            gridColumnCount = 1,
            isLoading = false,
            error = null,
            weekDayStreaks = emptyList()
        )
    }

    factory {
        BookListModel(
            defaultState = get<BookListState>(),
            bookRepository = get<BookRepository>(),
            readingSessionRepository = get<ReadingSessionRepository>()
        )
    }
}
