package fuwafuwa.time.bookechi.ui.feature.book_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel

data object BookListScreen : Screen

@Composable
fun BookListScreen(
    viewModel: BookListViewModel
) {
    val state by viewModel.model.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenState(state)
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
    LazyColumn {
        items(books) { book ->
            BookItem(book)
        }
    }
}

@Composable
private fun BookItem(book: Book) {
    Card {
        Row {
//            Image()
            Column {
                Text(book.name)
                Text(book.author)
            }
            Text("${book.currentPage} / ${book.pages}")
        }
    }
}
