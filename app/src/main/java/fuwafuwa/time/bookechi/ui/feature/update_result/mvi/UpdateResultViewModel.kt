package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class UpdateResultViewModel(
    override val model: UpdateResultModel
) : BaseViewModel<UpdateResultAction, UpdateResultState>()
