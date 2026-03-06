package fuwafuwa.time.bookechi.ui.feature.productivity.di

import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityModel
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productivityModule = module {
    singleOf(::ProductivityModel) { bind<ProductivityModel>() }

    viewModelOf(::ProductivityViewModel)

    factory {
        ProductivityState(
            booksRead = 0,
            pagesRead = 0,
            dayStreak = 0,
            averagePages = 0f,
        )
    }

    factory {
        ProductivityViewModel(
            model = get()
        )
    }
}
