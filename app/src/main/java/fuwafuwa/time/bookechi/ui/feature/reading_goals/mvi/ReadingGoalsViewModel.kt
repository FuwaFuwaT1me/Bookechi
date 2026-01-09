package fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class ReadingGoalsViewModel(
    override val model: ReadingGoalsModel
) : BaseViewModel<ReadingGoalsAction, ReadingGoalsState>()

