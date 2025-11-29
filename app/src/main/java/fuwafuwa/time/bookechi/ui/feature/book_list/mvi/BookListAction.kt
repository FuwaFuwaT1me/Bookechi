package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface BookListAction : Action {
    data object LoadBooks : BookListAction
    data object RefreshBooks : BookListAction
}
