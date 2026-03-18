package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListAction
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.DayStreak
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
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        BookListContent(
            state = state,
            onAction = onAction
        )
    }
}

@Composable
private fun BookListContent(
    state: BookListState,
    onAction: (BookListAction) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(state.gridColumnCount),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        bookListHeader(state)
        bookListBody(state, onAction)
    }
}

private fun LazyGridScope.bookListHeader(state: BookListState) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(4.dp))
    }
    item(span = { GridItemSpan(maxLineSpan) }) {
        Header(state)
    }
}

private fun LazyGridScope.bookListBody(
    state: BookListState,
    onAction: (BookListAction) -> Unit
) {
    when {
        state.isLoading -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        state.error != null -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Something went wrong")
            }
        }

        state.books.isEmpty() -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyBookList(
                    onAddBookClick = {
                        onAction(BookListAction.NavigateToAddBook)
                    }
                )
            }
        }

        else -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Сейчас читаете",
                    color = FigmaTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.books, key = { it.id }) { book ->
                NewBookItem(
                    modifier = Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null
                    ),
                    book = book,
                    onBookClick = {
                        onAction(BookListAction.NavigateToBookDetails(book))
                    },
                    onEditBookClick = {
                        onAction(BookListAction.NavigateToEditBook(book))
                    },
                    onDeleteBookClick = {
                        onAction(BookListAction.DeleteBook(book))
                    }
                )
            }
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
            text = if (state.isTodayStreak) {
                "Стрик продлен, так держать!"
            } else {
                "Ты уже читал сегодня?"
            },
            fontSize = 30.sp,
            color = FigmaTitle,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        StreakPanel(state)
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
                            pages = (100..250).random(),
                            isFavorite = false,
                        )
                    )
                }
            },
            totalDaysWithStreak = 10,
            isTodayStreak = false,
            weekDayStreaks = listOf(
                DayStreak(true, false),
                DayStreak(false, false),
                DayStreak(false, false),
                DayStreak(false, true),
                DayStreak(false, false),
                DayStreak(false, false),
                DayStreak(false, false),
            )
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
            books = emptyList(),
            totalDaysWithStreak = 10,
            isTodayStreak = false,
            weekDayStreaks = listOf(
                DayStreak(true, false),
                DayStreak(false, false),
                DayStreak(false, false),
                DayStreak(false, true),
                DayStreak(false, false),
                DayStreak(false, false),
                DayStreak(false, false),
            )
        ),
        onAction = {}
    )
}
