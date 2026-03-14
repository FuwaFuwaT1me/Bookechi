package fuwafuwa.time.bookechi.ui.feature.book_details.di

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsModel
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsState
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsViewModel
import kotlinx.coroutines.runBlocking
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookDetailsModule = module {
    viewModel { (book: Book) ->
        val state = BookDetailsState(book = book)
        val model = BookDetailsModel(
            defaultState = state,
            bookRepository = get<BookRepository>()
        )
        BookDetailsViewModel(model = model)
    }
}
