package fuwafuwa.time.bookechi.ui.feature.update_result.di

import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultArgs
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultModel
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultState
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val updateResultModule = module {
    viewModel { (args: UpdateResultArgs) ->
        val state = UpdateResultState(
            pagesDelta = args.updatedPages - args.startPages,
            startPages = args.startPages,
            updatedPages = args.updatedPages,
            allBookPages = args.bookAllPages,
            newStreakCount = 0,
            bookId = args.bookId,
            showStreakIntro = args.streakExtended,
            readingTimeMinutes = args.readingTimeMinutes,
            bookName = args.bookName,
            bookAuthor = args.bookAuthor,
            coverPath = args.coverPath.ifBlank { null },
        )
        val model = UpdateResultModel(
            defaultState = state,
            readingSessionRepository = get(),
            bookRepository = get(),
        )
        UpdateResultViewModel(model = model)
    }
}
