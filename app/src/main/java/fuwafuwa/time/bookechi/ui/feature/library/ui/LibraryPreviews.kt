package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState

@Preview
@Composable
private fun LibraryFilledPreview() {
    LibraryContent(
        state = LibraryState(
            books = LibraryPreviewData.books(),
            isLoading = false
        )
    )
}

@Preview
@Composable
private fun LibraryLoadingPreview() {
    LibraryContent(
        state = LibraryState(
            books = LibraryPreviewData.books(),
            isLoading = true
        )
    )
}

@Preview
@Composable
private fun LibraryEmptyPreview() {
    LibraryContent(
        state = LibraryState(
            books = emptyList(),
            isLoading = false
        )
    )
}
