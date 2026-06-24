package fuwafuwa.time.bookechi.ui.feature.read_shelf.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfAction
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfBookItem
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfMonth
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfState
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.serialization.Serializable
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Serializable
data object ReadShelfScreen : Screen

@Composable
fun ShelfScreen(viewModel: ShelfViewModel) {
    val state by viewModel.model.state.collectAsState()
    ShelfContent(state = state, onAction = viewModel::sendAction)
}

@Composable
private fun ShelfContent(
    state: ShelfState,
    onAction: (ShelfAction) -> Unit,
) {
    val colors = BookechiTheme.colors
    val context = LocalContext.current
    val shareText = stringResource(R.string.shelf_share_text, state.totalCount)

    Column(modifier = Modifier.fillMaxSize().background(colors.canvas).statusBarsPadding()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            CircleIcon(Icons.AutoMirrored.Filled.ArrowBack) { onAction(ShelfAction.NavigateBack) }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.shelf_crumb),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                Text(
                    text = stringResource(R.string.shelf_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = state.totalCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentDeep,
                )
                Text(
                    text = pluralStringResource(R.plurals.shelf_books_word, state.totalCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
            }
        }

        Spacer(Modifier.height(Spacing.sm))
        // Sort toggle (single mode in v1)
        Row(
            modifier = Modifier
                .padding(horizontal = Spacing.xl)
                .clip(CircleShape)
                .background(colors.chipBg)
                .padding(horizontal = Spacing.md, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(14.dp))
            Text(stringResource(R.string.shelf_sort_recent), style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(start = Spacing.xl, end = Spacing.xl, top = Spacing.md, bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            if (state.months.isEmpty() && !state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 80.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.shelf_empty), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    }
                }
            }
            state.months.forEach { month ->
                item(key = "${month.year}-${month.monthValue}") {
                    MonthSection(month = month, onOpen = { onAction(ShelfAction.OpenBook(it)) })
                }
            }
        }

        // Share
        PrimaryButton(
            text = stringResource(R.string.shelf_share),
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(intent, null))
            },
            modifier = Modifier
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.md)
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun MonthSection(month: ShelfMonth, onOpen: (Long) -> Unit) {
    val colors = BookechiTheme.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = monthLabel(month),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.accentDeep,
            )
            Box(Modifier.weight(1f).height(1.dp).background(colors.divider))
            Text(
                text = pluralStringResource(R.plurals.lib_books_count, month.books.size, month.books.size),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
        }
        ShelfBoard(books = month.books, onOpen = onOpen)
    }
}

/** Полка-доска с обложками. Доска неподвижна, по горизонтали скроллятся только книги. */
@Composable
private fun ShelfBoard(books: List<ShelfBookItem>, onOpen: (Long) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Доска — фиксированная, во всю ширину, не скроллится.
        WoodPlank(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
        // Книги скроллятся поверх доски.
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = Spacing.xl, end = Spacing.xl, bottom = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Bottom,
        ) {
            books.forEach { item ->
                ShelfCover(item = item, onClick = { onOpen(item.bookId) })
            }
        }
    }
}

@Composable
private fun WoodPlank(modifier: Modifier = Modifier) {
    val dark = BookechiTheme.colors.isDark
    val topA = if (dark) Color(0xFF6E5638) else Color(0xFFE6C79E)
    val topB = if (dark) Color(0xFF5C4630) else Color(0xFFD3A674)
    val edgeA = if (dark) Color(0xFF4A3826) else Color(0xFFB78C58)
    val edgeB = if (dark) Color(0xFF3A2C1E) else Color(0xFF9C744A)
    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp, topStart = 3.dp, topEnd = 3.dp))
            .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp, topStart = 3.dp, topEnd = 3.dp)),
    ) {
        Box(Modifier.fillMaxWidth().height(9.dp).background(Brush.verticalGradient(listOf(topA, topB))))
        Box(Modifier.fillMaxWidth().height(11.dp).background(Brush.verticalGradient(listOf(edgeA, edgeB))))
    }
}

@Composable
private fun ShelfCover(item: ShelfBookItem, onClick: () -> Unit) {
    val coverShape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    Box {
        BookCover(
            coverPath = item.coverPath,
            title = item.title,
            author = item.author,
            width = 84.dp,
            shape = coverShape,
            modifier = Modifier
                .shadow(6.dp, coverShape, clip = false)
                .clip(coverShape)
                .clickable(onClick = onClick),
        )
        if (item.favorite) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(14.dp),
            )
        }
    }
}

@Composable
private fun CircleIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun monthLabel(month: ShelfMonth): String {
    if (month.monthValue == 0) return stringResource(R.string.shelf_no_date)
    val name = Month.of(month.monthValue)
        .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
        .replaceFirstChar { it.uppercase() }
    return "$name ${month.year}"
}

/* ----- Previews ----- */

private fun previewShelfState(): ShelfState {
    val today = java.time.LocalDate.of(2026, 6, 12)
    fun b(id: Long, title: String, author: String, fav: Boolean, off: Long) =
        ShelfBookItem(id, title, author, null, fav, today.minusDays(off))
    return ShelfState(
        months = listOf(
            ShelfMonth(2026, 6, listOf(b(1, "Над пропастью во ржи", "Дж. Д. Сэлинджер", true, 0))),
            ShelfMonth(2026, 5, listOf(
                b(2, "1984", "Джордж Оруэлл", false, 35),
                b(3, "Мастер и Маргарита", "Михаил Булгаков", true, 40),
            )),
            ShelfMonth(2026, 4, listOf(
                b(4, "Сто лет одиночества", "Габриэль Гарсиа Маркес", false, 70),
                b(5, "Великий Гэтсби", "Фрэнсис Фицджеральд", false, 75),
            )),
        ),
        totalCount = 12,
        isLoading = false,
    )
}

@Preview(name = "Shelf Light", showBackground = true, backgroundColor = 0xFFF4ECE1, heightDp = 900)
@Composable
private fun ShelfPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ShelfContent(state = previewShelfState(), onAction = {})
        }
    }
}

@Preview(name = "Shelf Dark", showBackground = true, backgroundColor = 0xFF1C1611, heightDp = 900)
@Composable
private fun ShelfPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            ShelfContent(state = previewShelfState(), onAction = {})
        }
    }
}
