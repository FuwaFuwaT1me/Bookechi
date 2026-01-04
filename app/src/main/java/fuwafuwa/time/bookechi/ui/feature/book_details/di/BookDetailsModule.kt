package fuwafuwa.time.bookechi.ui.feature.book_details.di

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsModel
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsState
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookDetailsModule = module {
    viewModel { (bookId: Long) ->
        val bookRepository = get<BookRepository>()
        val book = kotlinx.coroutines.runBlocking {
            bookRepository.getBookById(bookId)
        }
        val state = BookDetailsState(book = book)
        val model = BookDetailsModel(
            defaultState = state,
            bookRepository = bookRepository
        )
        BookDetailsViewModel(model = model)
    }
}

