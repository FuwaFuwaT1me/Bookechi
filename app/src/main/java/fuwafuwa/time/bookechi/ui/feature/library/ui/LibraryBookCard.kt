package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.ProgressBar
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.StatusChip
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

// Высота, зарезервированная под 2 строки названия (titleSmall) — чтобы низ
// карточек в ряду выравнивался независимо от длины заголовка.
private val TitleReservedHeight = 40.dp

// Фиксированная высота мета-строки (ProgressBar+процент / StatusChip), чтобы
// все ячейки сетки были одинаковой высоты.
private val MetaReservedHeight = 28.dp

// Размер иконки-сердечка поверх обложки.
private val HeartIconSize = 20.dp

/**
 * Карточка книги в сетке «Библиотека».
 *
 * Раскладка строго вертикальная (Column) с детерминированной высотой:
 * обложка держит соотношение 2:3 сама (DS [BookCover]); название резервирует
 * место под 2 строки; мета-строка имеет фиксированную высоту. За счёт этого
 * все карточки в ряду одной высоты, и LazyVerticalGrid не «едет».
 */
@Composable
fun LibraryBookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surfaceElevated, DsShapes.card)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // Обложка во всю ширину карточки + сердечко в правом верхнем углу.
        Row(modifier = Modifier.fillMaxWidth()) {
            BookCover(
                coverPath = book.coverPath,
                title = book.name,
                author = book.author,
                width = null,
                modifier = Modifier.weight(1f),
            )

            // Сердечко — голая иконка поверх обложки (как в макете), без подложки-кружка.
            Icon(
                imageVector = if (book.isFavorite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = null,
                tint = if (book.isFavorite) colors.accent else colors.textSecondary,
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(Spacing.sm)
                    .size(HeartIconSize),
            )
        }

        // Название: 2 строки с зарезервированной высотой (одинаковый низ у карточек).
        Text(
            text = book.name,
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
            ),
            color = colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TitleReservedHeight),
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

        // Мета-строка фиксированной высоты: прогресс для «Читаю», иначе статус-чип.
        Box(
            modifier = Modifier
                .fillMaxWidth(),
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

private fun statusLabel(status: ReadingStatus): String = when (status) {
    ReadingStatus.None -> "Не начата"
    ReadingStatus.Planned -> "В планах"
    ReadingStatus.Reading -> "Читаю"
    ReadingStatus.Paused -> "Пауза"
    ReadingStatus.Dropped -> "Брошена"
    ReadingStatus.Completed -> "Прочитано"
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
