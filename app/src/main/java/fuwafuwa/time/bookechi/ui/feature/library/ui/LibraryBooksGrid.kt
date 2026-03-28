package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.data.model.Book

@Composable
fun LibraryBooksGrid(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    onEditClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            books, key = { index, book -> book.id }
        ) { index, book ->
            Column {
                LibraryBookCard(
                    book = book,
                    onClick = { onBookClick(book) },
                    onEditClick = { onEditClick(book) }
                )

                if (index + 2 > books.size) {
                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun LibraryBooksGridPreview() {
    LibraryBooksGrid(
        books = LibraryPreviewData.books(),
        onBookClick = {},
        onEditClick = {}
    )
}
