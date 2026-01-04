package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class BookDetailsViewModel(
    override val model: BookDetailsModel
) : BaseViewModel<BookDetailsAction, BookDetailsState>()

