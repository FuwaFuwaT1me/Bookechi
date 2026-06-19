package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Сетка библиотеки (masonry): 2 колонки — Fixed(2) сам делит доступную ширину
 * пополам минус интервал/паддинги. Карточки разной высоты укладываются «кладкой»
 * без выравнивания по рядам, поэтому [LibraryBookCard] может иметь произвольную
 * высоту (без фиксированных резервов).
 */
@Composable
fun LibraryBooksGrid(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: (Book) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(bottom = 96.dp),
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = Spacing.md,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = contentPadding,
    ) {
        items(books, key = { it.id }) { book ->
            LibraryBookCard(
                book = book,
                onClick = { onBookClick(book) },
                onToggleFavorite = { onToggleFavorite(book) },
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
