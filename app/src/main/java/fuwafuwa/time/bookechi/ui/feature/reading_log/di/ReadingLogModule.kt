package fuwafuwa.time.bookechi.ui.feature.reading_log.di

import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.ReadingLogModel
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.ReadingLogState
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.ReadingLogViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val readingLogModule = module {
    viewModel { (bookId: Long) ->
        ReadingLogViewModel(
            model = ReadingLogModel(
                defaultState = ReadingLogState(
                    bookId = bookId,
                    isBookScope = bookId > 0,
                ),
                bookRepository = get(),
                readingSessionRepository = get(),
            )
        )
    }
}
