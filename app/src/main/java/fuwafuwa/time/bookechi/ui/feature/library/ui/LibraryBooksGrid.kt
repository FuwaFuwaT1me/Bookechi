package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Сетка библиотеки: 2 фиксированные колонки, равные интервалы по обеим осям.
 * Карточки имеют детерминированную высоту (см. [LibraryBookCard]), поэтому
 * ячейки в ряду выравниваются по низу и раскладка не «едет».
 */
@Composable
fun LibraryBooksGrid(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 96.dp),
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = contentPadding,
    ) {
        items(books, key = { it.id }) { book ->
            LibraryBookCard(
                book = book,
                onClick = { onBookClick(book) },
            )
        }
    }
}

@Preview(name = "LibraryBooksGrid Light", showBackground = true, backgroundColor = 0xFFFFF9F6)
@Composable
private fun LibraryBooksGridPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryBooksGrid(
                books = LibraryPreviewData.books(),
                onBookClick = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@Preview(name = "LibraryBooksGrid Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun LibraryBooksGridPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryBooksGrid(
                books = LibraryPreviewData.books(),
                onBookClick = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}
