package fuwafuwa.time.bookechi.ui.feature.add_book.di

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookModel
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import fuwafuwa.time.bookechi.utils.file.CacheHelper
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addBookModule = module {
    singleOf(::AddBookModel) { bind<AddBookModel>() }

    viewModelOf(::AddBookViewModel)

    single { CacheHelper(get()) }

    factory {
        AddBookState(
            bookName = "",
            bookAuthor = "",
            bookCoverPath = null,
            readingNow = false,
            bookPages = 0,
            bookCurrentPage = 0,
            isBookCoverLoading = false,
            bookCoverError = null,
        )
    }

    factory {
        AddBookModel(
            defaultState = get<AddBookState>(),
            bookRepository = get<BookRepository>(),
            cacheHelper = get<CacheHelper>()
        )
    }

    factory {
        AddBookViewModel(
            model = get<AddBookModel>()
        )
    }
}
