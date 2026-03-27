package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import androidx.compose.runtime.Composable
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import kotlinx.serialization.Serializable

@Serializable
data object AddBookScreen : Screen

@Composable
fun AddBookScreen(
    viewModel: AddBookViewModel
) {
    AddBookScreenV2(viewModel)
}
