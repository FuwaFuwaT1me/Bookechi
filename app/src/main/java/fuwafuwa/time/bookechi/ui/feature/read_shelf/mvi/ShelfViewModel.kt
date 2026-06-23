package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class ShelfViewModel(
    override val model: ShelfModel
) : BaseViewModel<ShelfAction, ShelfState>()
