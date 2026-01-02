package fuwafuwa.time.bookechi.ui.feature.book_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.NavigateToAddBook
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel
import kotlinx.serialization.Serializable

@Serializable
data object BookListScreen : Screen

@Composable
fun BookListScreen(
    viewModel: BookListViewModel
) {
    val state by viewModel.model.state.collectAsState()

    BookListScreenPrivate(
        state = state,
        onAddBookClick = {
            viewModel.sendNavigationEvent(NavigateToAddBook())
        }
    )
}

@Composable
private fun BookListScreenPrivate(
    state: BookListState,
    onAddBookClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .fillMaxSize()
    ) {
        ScreenState(state)
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = {
                onAddBookClick()
            }
        ) {
            Icon(
                Icons.Default.Add,
                ""
            )
        }
    }
}

@Composable
private fun ScreenState(state: BookListState) {
    when {
        state.isLoading -> {
            CircularProgressIndicator()
        }

        state.error != null -> {
            Text("Something went wrong")
        }

        state.books.isNotEmpty() -> {
            BooksList(state.books)
        }
    }
}

@Composable
private fun BooksList(books: List<Book>) {
    LazyColumn{
        items(books) { book ->
            BookItem(book)
        }
    }
}

@Composable
private fun BookItem(book: Book) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(book.coverPath)
                    .build(),
                placeholder = painterResource(R.drawable.empty_book_cover_placeholder),
                contentDescription = "",
                modifier = Modifier.size(100.dp)
            )
            Column {
                Text(book.name)
                Text(book.author)
            }
            Text("${book.currentPage} / ${book.pages}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookListScreenPreview() {
    BookListScreenPrivate(
        state = BookListState(
            isLoading = false,
            error = null,
            books = listOf(
                Book(
                    name = "Book 1",
                    author = "Author 1",
                    coverPath = "https://picsum.photos/200/300",
                    currentPage = 1,
                    pages = 100
                )
            )
        ),
        onAddBookClick = {}
    )
}
