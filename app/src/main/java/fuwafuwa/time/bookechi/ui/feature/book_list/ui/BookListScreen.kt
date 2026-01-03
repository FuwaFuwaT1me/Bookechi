package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.BookCover
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.NavigateToAddBook
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel
import fuwafuwa.time.bookechi.ui.theme.BlueMain
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
            containerColor = BlueMain,
            contentColor = Color.White,
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
            Content(state)
        }
    }
}

@Composable
private fun Content(state: BookListState) {
    Column {
        Header()
        BooksList(state.books)
    }
}

@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ,
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
            ,
            textAlign = TextAlign.Center,
            text = "All books",
            fontSize = 24.sp,
            color = BlueMain,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BooksList(books: List<Book>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(books) { book ->
            BookItem(
                book
            )
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
            books = buildList {
                repeat(10) {
                    add(
                        Book(
                            name = "Book 1",
                            author = "Author 1",
                            coverPath = "https://picsum.photos/200/300",
                            currentPage = (0..100).random(),
                            pages = (100..250).random()
                        )
                    )
                }
            }
        ),
        onAddBookClick = {}
    )
}
