package fuwafuwa.time.bookechi.ui.feature.reading_log.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class ReadingLogViewModel(
    override val model: ReadingLogModel
) : BaseViewModel<ReadingLogAction, ReadingLogState>()
