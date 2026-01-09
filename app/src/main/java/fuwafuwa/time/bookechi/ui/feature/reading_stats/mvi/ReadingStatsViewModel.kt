package fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class ReadingStatsViewModel(
    override val model: ReadingStatsModel
) : BaseViewModel<ReadingStatsAction, ReadingStatsState>()

