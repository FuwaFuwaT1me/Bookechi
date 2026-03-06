package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseModel

class ProductivityModel(
    defaultState: ProductivityState
): BaseModel<ProductivityState, ProductivityAction>(defaultState) {

    override fun onAction(action: ProductivityAction) {

    }
}
