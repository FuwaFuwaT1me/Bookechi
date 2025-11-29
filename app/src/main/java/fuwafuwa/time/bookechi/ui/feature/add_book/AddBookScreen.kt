package fuwafuwa.time.bookechi.ui.feature.add_book

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookAction
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data object AddBookScreen : Screen

@Composable
fun AddBookScreen(
    viewModel: AddBookViewModel
) {
    val state by viewModel.model.state.collectAsState()
    val bookNameState = rememberTextFieldState("")
    val bookAuthorState = rememberTextFieldState("")

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            TextField(
                state = bookNameState
            )
            TextField(
                state = bookAuthorState
            )
            Button(
                onClick = {
                    viewModel.sendAction(
                        AddBookAction.SaveBook(
                            bookName = bookNameState.text.toString(),
                            bookAuthor = bookAuthorState.text.toString()
                        )
                    )
                }
            ) {
                Text("Save book")
            }
        }
    }
}
