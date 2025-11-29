package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class BookListViewModel(
    override val model: BookListModel
) : BaseViewModel<BookListAction, BookListState>() {

}
