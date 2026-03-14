package fuwafuwa.time.bookechi.ui.feature.library.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class LibraryViewModel(
    override val model: LibraryModel
) : BaseViewModel<LibraryAction, LibraryState>()
