package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import fuwafuwa.time.bookechi.ui.theme.FigmaAddBookBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
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
            Header(state)

            ScreenState(
                state = state,
                onAction = onAction
            )
        }

        if (state.books.isNotEmpty()) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 8.dp)
                ,
                containerColor = FigmaAddBookBackground,
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
                state = state,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun Header(state: BookListState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ,
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Start)
            ,
            text = "Доброе утро, Иван!",
            fontSize = 16.sp,
            color = FigmaSubtitle,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier
                .align(Alignment.Start)
            ,
            text = "Ты уже читал сегодня?",
            fontSize = 30.sp,
            color = FigmaTitle,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        StreakPanel(state)
    }
}

@Composable
private fun BooksList(
    state: BookListState,
    onAction: (BookListAction) -> Unit,
) {
    Column {

        Text(
            text = "Сейчас читаете",
            color = FigmaTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(state.gridColumnCount),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(state.books, key = { it.id }) { book ->
                NewBookItem(
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
}

@Preview(showBackground = true)
@Composable
private fun BookListScreenPreview() {
    BookListScreenPrivate(
        state = BookListState(
            isLoading = false,
            gridColumnCount = 1,
            error = null,
            books = buildList {
                repeat(10) {
                    add(
                        Book(
                            id = it.toLong(),
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

@Preview(showBackground = true)
@Composable
private fun EmptyBookListScreenPreview() {
    BookListScreenPrivate(
        state = BookListState(
            isLoading = false,
            gridColumnCount = 1,
            error = null,
            books = emptyList()
        ),
        onAction = {}
    )
}
