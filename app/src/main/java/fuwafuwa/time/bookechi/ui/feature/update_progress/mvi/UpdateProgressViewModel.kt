package fuwafuwa.time.bookechi.ui.feature.update_progress.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class UpdateProgressViewModel(
    override val model: UpdateProgressModel
) : BaseViewModel<UpdateProgressAction, UpdateProgressState>()
