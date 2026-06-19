package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Круглый FAB «+» на accent. */
@Composable
fun LibraryAddBookFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = modifier
            .padding(Spacing.xl)
            .size(56.dp)
            .clip(CircleShape)
            .background(colors.accent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.lib_add_book),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Preview(name = "LibraryAddBookFab Light", showBackground = true, backgroundColor = 0xFFFFF9F6)
@Composable
private fun LibraryAddBookFabPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryAddBookFab(onClick = {})
        }
    }
}

@Preview(name = "LibraryAddBookFab Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun LibraryAddBookFabPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryAddBookFab(onClick = {})
        }
    }
}
