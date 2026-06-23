package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class ShelfBookViewModel(
    override val model: ShelfBookModel
) : BaseViewModel<ShelfBookAction, ShelfBookState>()
