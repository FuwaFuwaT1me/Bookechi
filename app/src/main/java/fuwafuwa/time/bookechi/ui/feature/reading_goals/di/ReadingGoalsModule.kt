package fuwafuwa.time.bookechi.ui.feature.reading_goals.di

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi.ReadingGoalsModel
import fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi.ReadingGoalsState
import fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi.ReadingGoalsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val readingGoalsModule = module {
    viewModelOf(::ReadingGoalsViewModel)

    factory {
        ReadingGoalsState()
    }

    factory {
        ReadingGoalsModel(
            defaultState = get<ReadingGoalsState>(),
            bookRepository = get<BookRepository>(),
            readingSessionRepository = get<ReadingSessionRepository>()
        )
    }
}

