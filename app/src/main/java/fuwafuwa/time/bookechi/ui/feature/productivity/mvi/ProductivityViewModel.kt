package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class ProductivityViewModel(
    override val model: ProductivityModel
) : BaseViewModel<ProductivityAction, ProductivityState>() {
}
