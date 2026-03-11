package fuwafuwa.time.bookechi.ui.feature.productivity.di

import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityModel
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityViewModel
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productivityModule = module {
    singleOf(::ProductivityModel) { bind<ProductivityModel>() }

    viewModelOf(::ProductivityViewModel)

    factory {
        ProductivityState(
            booksRead = 6,
            pagesRead = 18574,
            dayStreak = 280,
            averagePages = 12.5f,
            sessions = ProductivityPreviewData.generateYearData()
        )
    }

    factory {
        ProductivityViewModel(
            model = get()
        )
    }
}
