package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.CornerBadgeSpec
import fuwafuwa.time.bookechi.base.ui.ds.CoverCornerCutoutShape
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.ProgressBar
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.StatusChip
import fuwafuwa.time.bookechi.base.ui.ds.badgeShape
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

// Скругление обложки (совпадает с DsShapes.cover).
private val CoverRadius = 12.dp

// Бейдж-лайк, влитый в верхний правый угол обложки: верх-правый угол скруглён как
// угол обложки, верх-левый и низ-правый — мелкое скругление, низ-левый — большой
// полукруг внутрь обложки. Вокруг бейджа в обложке вырезан гладкий «ров» (gap).
private val FavoriteBadge = CornerBadgeSpec(
    size = 40.dp,
    gap = 5.dp,
    cornerOuter = CoverRadius,
    cornerSmall = 8.dp,
    cornerInner = 22.dp,
)

// Размер сердечка внутри бейджа.
private val HeartIconSize = 20.dp

/**
 * Карточка книги в сетке «Библиотека» (masonry, произвольная высота).
 *
 * Обложка во всю ширину карточки с гладким вырезом в верхнем правом углу, в
 * который вложен бейдж-лайк. Ниже — название (serif), автор и мета-строка
 * (прогресс для «Читаю», иначе статус-чип).
 */
@Composable
fun LibraryBookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: () -> Unit = {},
) {
    val colors = BookechiTheme.colors
    val coverShape = remember { CoverCornerCutoutShape(CoverRadius, FavoriteBadge) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surfaceElevated, DsShapes.card)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // Обложка с вырезом + бейдж-лайк, влитый в верхний правый угол.
        Box(modifier = Modifier.fillMaxWidth()) {
            BookCover(
                coverPath = book.coverPath,
                title = book.name,
                author = book.author,
                width = null,
                shape = coverShape,
                titleEndInset = FavoriteBadge.size,
                modifier = Modifier.fillMaxWidth(),
            )

            val badgeColor = if (book.isFavorite) colors.accent else colors.accentSoft
            val heartTint = if (book.isFavorite) Color.White else colors.accentDeep
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(FavoriteBadge.size)
                    .clip(FavoriteBadge.badgeShape())
                    .background(badgeColor)
                    .clickable(onClick = onToggleFavorite),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (book.isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = stringResource(R.string.lib_favorite),
                    tint = heartTint,
                    modifier = Modifier.size(HeartIconSize),
                )
            }
        }

        // Название: до 2 строк, serif bold.
        Text(
            text = book.name,
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
            ),
            color = colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )

        // Автор: одна строка.
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )

        // Мета-строка: прогресс для «Читаю», иначе статус-чип.
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (book.readingStatus == ReadingStatus.Reading) {
                val progress = if (book.pages > 0) {
                    book.currentPage.toFloat() / book.pages.toFloat()
                } else {
                    0f
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    ProgressBar(
                        progress = progress,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.accentDeep,
                    )
                }
            } else {
                StatusChip(status = statusLabel(book.readingStatus))
            }
        }
    }
}

@Composable
private fun statusLabel(status: ReadingStatus): String = when (status) {
    ReadingStatus.None -> stringResource(R.string.lib_status_none)
    ReadingStatus.Planned -> stringResource(R.string.lib_status_planned)
    ReadingStatus.Reading -> stringResource(R.string.lib_status_reading)
    ReadingStatus.Paused -> stringResource(R.string.lib_status_paused)
    ReadingStatus.Dropped -> stringResource(R.string.lib_status_dropped)
    ReadingStatus.Completed -> stringResource(R.string.lib_status_completed)
}

@Preview(name = "LibraryBookCard Reading Light", showBackground = true, backgroundColor = 0xFFFFF9F6)
@Composable
private fun LibraryBookCardReadingPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                LibraryBookCard(
                    book = LibraryPreviewData.books()[0].copy(isFavorite = true),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                LibraryBookCard(
                    book = LibraryPreviewData.books()[2].copy(readingStatus = ReadingStatus.Planned),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Preview(name = "LibraryBookCard Reading Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun LibraryBookCardReadingPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                LibraryBookCard(
                    book = LibraryPreviewData.books()[0].copy(isFavorite = true),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                LibraryBookCard(
                    book = LibraryPreviewData.books()[2].copy(readingStatus = ReadingStatus.Planned),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
