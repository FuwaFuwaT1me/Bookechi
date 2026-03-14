package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryModel
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryViewModel
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Serializable
data object LibraryScreen : Screen

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel
) {
    val state by viewModel.model.state.collectAsState()

    LibraryContent(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Preview
@Composable
private fun LibraryScreenPreview() {
    val viewModel = remember { previewLibraryViewModel() }
    LibraryScreen(viewModel)
}

private fun previewLibraryViewModel(): LibraryViewModel {
    val previewBooks = LibraryPreviewData.books()
    val fakeDao = object : BookDao {
        override fun getAllBooks(): Flow<List<Book>> = flowOf(previewBooks)

        override suspend fun getBookById(id: Long): Book = previewBooks.first()

        override suspend fun insertBook(book: Book) = Unit

        override suspend fun insertBooks(books: List<Book>) = Unit

        override suspend fun updateBook(book: Book) = Unit

        override suspend fun deleteBook(book: Book) = Unit

        override suspend fun deleteBookById(id: Long) = Unit

        override suspend fun deleteAllBooks() = Unit
    }
    val repository = BookRepository(fakeDao)
    val model = LibraryModel(
        defaultState = LibraryState(
            books = previewBooks,
            isLoading = false,
            error = null
        ),
        bookRepository = repository
    )
    return LibraryViewModel(model)
}
