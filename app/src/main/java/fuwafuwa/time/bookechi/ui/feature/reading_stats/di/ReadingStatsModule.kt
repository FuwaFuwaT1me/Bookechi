package fuwafuwa.time.bookechi.ui.feature.reading_stats.di

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi.ReadingStatsModel
import fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi.ReadingStatsState
import fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi.ReadingStatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val readingStatsModule = module {
    viewModelOf(::ReadingStatsViewModel)

    factory {
        ReadingStatsState()
    }

    factory {
        ReadingStatsModel(
            defaultState = get<ReadingStatsState>(),
            bookRepository = get<BookRepository>(),
            readingSessionRepository = get<ReadingSessionRepository>()
        )
    }
}

