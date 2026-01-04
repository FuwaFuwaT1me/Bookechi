package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListAction
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
        onAction = viewModel::sendAction
    )
}

@Composable
private fun BookListScreenPrivate(
    state: BookListState,
    onAction: (BookListAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .fillMaxSize()
    ) {
        Column {
            Header()

            ScreenState(
                state = state,
                onAction = onAction
            )
        }

        if (state.books.isNotEmpty()) {
            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                containerColor = BlueMain,
                contentColor = Color.White,
                onClick = {
                    onAction(BookListAction.NavigateToAddBook)
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    ""
                )
            }
        }
    }
}

@Composable
private fun ScreenState(
    state: BookListState,
    onAction: (BookListAction) -> Unit,
) {
    when {
        state.isLoading -> {
            CircularProgressIndicator()
        }

        state.error != null -> {
            Text("Something went wrong")
        }

        state.books.isEmpty() -> {
            EmptyBookList(
                onAddBookClick = {
                    onAction(BookListAction.NavigateToAddBook)
                }
            )
        }

        else -> {
            BooksList(
                books = state.books,
                onAction = onAction,
            )
        }
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
private fun BooksList(
    books: List<Book>,
    onAction: (BookListAction) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(books, key = { it.id }) { book ->
            BookItem(
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null
                ),
                book = book,
                onClick = {
                    onAction(BookListAction.NavigateToBookDetails(book))
                },
                onDeleteBookClick = {
                    onAction(BookListAction.DeleteBook(book))
                }
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
        onAction = {}
    )
}
