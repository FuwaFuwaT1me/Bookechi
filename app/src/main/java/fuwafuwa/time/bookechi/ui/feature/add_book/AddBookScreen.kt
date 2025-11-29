package fuwafuwa.time.bookechi.ui.feature.add_book

import android.graphics.Paint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookAction
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
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
    val bookCoverPath = remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .fillMaxSize()
    ) {
        Column {
            BookCover(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(200.dp),
                state = state,
                onCoverChange = { cover ->
                    bookCoverPath.value = cover
                }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                state = bookNameState
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                state = bookAuthorState
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            onClick = {
                viewModel.sendAction(
                    AddBookAction.SaveBook(
                        bookName = bookNameState.text.toString(),
                        bookAuthor = bookAuthorState.text.toString(),
                        bookCoverPath = bookCoverPath.value
                    )
                )
            }
        ) {
            Text("Save book")
        }
    }
}

@Composable
private fun BookCover(
    state: AddBookState,
    onCoverChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageUri by remember { mutableStateOf<Uri?>(state.bookCoverPath.toUri()) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        onCoverChange(imageUri.toString())
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .build(),
        placeholder = painterResource(R.drawable.empty_book_cover_placeholder),
        error = painterResource(R.drawable.empty_book_cover_placeholder),
        contentDescription = "Выбранное изображение",
        modifier = modifier
            .clickable {
                galleryLauncher.launch("image/*")
            }
    )
}
