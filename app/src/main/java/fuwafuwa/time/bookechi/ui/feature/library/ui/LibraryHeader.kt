package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Шапка библиотеки: мелкая подпись «{N} книг» (bodyMedium, textSecondary)
 * и serif-заголовок «Библиотека» (headlineLarge).
 */
@Composable
fun LibraryHeader(
    booksCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = pluralStringResource(R.plurals.lib_books_count, booksCount, booksCount),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
        Text(
            text = stringResource(R.string.lib_title),
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary,
        )
    }
}

@Preview(name = "LibraryHeader Light", showBackground = true, backgroundColor = 0xFFFFF9F6)
@Composable
private fun LibraryHeaderPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryHeader(booksCount = 6, modifier = Modifier.padding(Spacing.lg))
        }
    }
}

@Preview(name = "LibraryHeader Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun LibraryHeaderPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryHeader(booksCount = 21, modifier = Modifier.padding(Spacing.lg))
        }
    }
}
