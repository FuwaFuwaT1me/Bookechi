package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class AddBookViewModel(
    override val model: AddBookModel
) : BaseViewModel<AddBookAction, AddBookState>() {
}
