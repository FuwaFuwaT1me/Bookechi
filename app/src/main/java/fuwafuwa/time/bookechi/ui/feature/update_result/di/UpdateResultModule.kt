package fuwafuwa.time.bookechi.ui.feature.update_result.di

import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultModel
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultState
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val updateResultModule = module {
    viewModel { (startPages: Int, updatedPages: Int, bookAllPages: Int) ->
        val state = UpdateResultState(
            pagesDelta = updatedPages - startPages,
            startPages = startPages,
            updatedPages = updatedPages,
            allBookPages = bookAllPages,
            newStreakCount = 0,
        )
        val model = UpdateResultModel(
            defaultState = state,
            readingSessionRepository = get()
        )
        UpdateResultViewModel(model = model)
    }
}
