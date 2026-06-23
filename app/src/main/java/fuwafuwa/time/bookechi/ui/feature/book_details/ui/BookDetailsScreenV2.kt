package fuwafuwa.time.bookechi.ui.feature.book_details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.EmptyState
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.ProgressBar
import fuwafuwa.time.bookechi.base.ui.ds.RatingSheet
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.StatusChip
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsAction
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookQuote
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsState
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlin.math.ceil

@Composable
fun BookDetailsScreenV2(
    viewModel: BookDetailsViewModel
) {
    val state by viewModel.model.state.collectAsState()

    BookDetailsScreenV2Content(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun BookDetailsScreenV2Content(
    state: BookDetailsState,
    onAction: (BookDetailsAction) -> Unit
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.accent)
                }
            }

            state.error != null -> {
                ErrorContent(onBack = { onAction(BookDetailsAction.NavigateBack) })
            }

            state.book != null -> {
                BookDetailsContent(
                    book = state.book,
                    recentSessionPages = state.recentSessionPages,
                    quotes = state.quotes,
                    rating = state.rating,
                    onAction = onAction
                )
            }
        }

        // Лист редактирования метаданных книги.
        BookDetailsEditSheet(state = state, onAction = onAction)

        // Шторка проставления оценки.
        if (state.isRatingSheetOpen) {
            RatingSheet(
                current = state.rating,
                onDismiss = { onAction(BookDetailsAction.CloseRatingSheet) },
                onSave = { onAction(BookDetailsAction.SetRating(it)) },
                onClear = { onAction(BookDetailsAction.SetRating(0)) },
            )
        }
    }
}

@Composable
private fun ErrorContent(onBack: () -> Unit) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.details_error_title),
            style = MaterialTheme.typography.headlineSmall,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = stringResource(R.string.details_error_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.xl))
        PrimaryButton(text = stringResource(R.string.details_back), onClick = onBack)
    }
}

@Composable
private fun BookDetailsContent(
    book: Book,
    recentSessionPages: List<Int>,
    quotes: List<BookQuote>,
    rating: Int,
    onAction: (BookDetailsAction) -> Unit
) {
    val progress = if (book.pages > 0) {
        (book.currentPage.toFloat() / book.pages).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl)
    ) {
        TopBar(
            isFavorite = book.isFavorite,
            onBackClick = { onAction(BookDetailsAction.NavigateBack) },
            onEditClick = { onAction(BookDetailsAction.OpenEdit) },
            onFavoriteClick = { onAction(BookDetailsAction.ToggleFavorite) }
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        HeaderSection(
            book = book,
            rating = rating,
            onRateClick = { onAction(BookDetailsAction.OpenRatingSheet) },
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        ProgressCard(
            currentPage = book.currentPage,
            totalPages = book.pages,
            progress = progress
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        PrimaryButton(
            text = stringResource(R.string.details_mark_progress),
            onClick = { onAction(BookDetailsAction.NavigateToUpdateProgress) }
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SectionLabel(text = stringResource(R.string.details_reading_history))
            if (recentSessionPages.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.log_all_sessions),
                    style = MaterialTheme.typography.labelLarge,
                    color = BookechiTheme.colors.accentDeep,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(BookDetailsAction.NavigateToReadingLog) }
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.md))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DsShapes.card)
                .clickable(enabled = recentSessionPages.isNotEmpty()) {
                    onAction(BookDetailsAction.NavigateToReadingLog)
                },
        ) {
            ReadingHistorySparkline(recentSessionPages = recentSessionPages)
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        SectionLabel(text = stringResource(R.string.details_quotes_and_notes))
        Spacer(modifier = Modifier.height(Spacing.md))

        val hasNote = book.note.isNotBlank()
        if (hasNote) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DsShapes.card)
                    .background(BookechiTheme.colors.surfaceElevated)
                    .border(1.dp, BookechiTheme.colors.stroke, DsShapes.card)
                    .padding(Spacing.lg),
            ) {
                Text(
                    text = book.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BookechiTheme.colors.textPrimary,
                )
            }
            if (quotes.isNotEmpty()) Spacer(modifier = Modifier.height(Spacing.md))
        }

        if (quotes.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                quotes.forEach { quote ->
                    QuoteCard(quote = quote)
                }
            }
        } else if (!hasNote) {
            EmptyState(
                icon = Icons.Default.FormatQuote,
                title = stringResource(R.string.details_quotes_empty_title),
                subtitle = stringResource(R.string.details_quotes_empty_subtitle),
                ctaText = null,
                onCta = null
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun RatingRow(rating: Int, onClick: () -> Unit) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(5) { i ->
                Icon(
                    imageVector = if (i < rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = null,
                    tint = if (i < rating) colors.accent else colors.textSecondary.copy(alpha = 0.35f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Text(
            text = if (rating > 0) "$rating,0" else stringResource(R.string.details_rate_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = if (rating > 0) colors.accentDeep else colors.textSecondary,
        )
    }
}

@Composable
private fun TopBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.details_back),
            onClick = onBackClick
        )

        Spacer(modifier = Modifier.weight(1f))

        CircleIconButton(
            icon = Icons.Default.Edit,
            contentDescription = stringResource(R.string.details_edit),
            onClick = onEditClick
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        CircleIconButton(
            icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) stringResource(R.string.details_favorite_remove) else stringResource(R.string.details_favorite_add),
            onClick = onFavoriteClick,
            tint = if (isFavorite) colors.accent else colors.textSecondary
        )
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = BookechiTheme.colors.textPrimary
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.stroke, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun HeaderSection(
    book: Book,
    rating: Int,
    onRateClick: () -> Unit,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Высота обложки = ширина × 3/2 (соотношение 2:3): к ней привязываем колонку.
        val coverWidth = 104.dp
        val coverHeight = coverWidth * 1.5f

        BookCover(
            coverPath = book.coverPath,
            title = book.name,
            author = book.author,
            width = coverWidth
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = coverHeight),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Верх: название + автор.
            Column {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }

            // Низ (к кромке обложки): статус + оценка.
            Column {
                StatusChip(status = readingStatusLabel(book.readingStatus))
                Spacer(modifier = Modifier.height(Spacing.sm))
                RatingRow(rating = rating, onClick = onRateClick)
            }
        }
    }
}

@Composable
private fun ProgressCard(
    currentPage: Int,
    totalPages: Int,
    progress: Float
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.stroke, DsShapes.card)
            .padding(Spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.details_page_of, currentPage, totalPages),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                color = colors.accentDeep
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        ProgressBar(progress = progress, height = 10.dp)

        // Прогноз темпа временно убран из UI (логика paceForecast сохранена ниже).
    }
}

@Composable
private fun ReadingHistorySparkline(recentSessionPages: List<Int>) {
    val colors = BookechiTheme.colors
    // TODO: данные из ReadingSessionRepository.getSessionsForBook(bookId).
    val days = 10
    val recent = recentSessionPages.takeLast(days)
    val maxPages = (recent.maxOrNull() ?: 0).coerceAtLeast(1)
    // Всегда окно из `days` дней: недостающие слева — пустые дни, чтобы при малом
    // числе сессий столбик не растягивался на всю ширину.
    val slots: List<Int> = List(days - recent.size) { 0 } + recent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.stroke, DsShapes.card)
            .padding(Spacing.lg)
    ) {
        if (recent.isEmpty()) {
            Text(
                text = stringResource(R.string.details_no_reading_records),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        } else {
            val barShape = RoundedCornerShape(
                topStart = 6.dp,
                topEnd = 6.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                slots.forEach { value ->
                    if (value <= 0) {
                        // Пустой день — тонкий стаб у базовой линии.
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(barShape)
                                .background(colors.stroke)
                        )
                    } else {
                        val ratio = value.toFloat() / maxPages
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(ratio.coerceIn(0.12f, 1f))
                                .clip(barShape)
                                .background(heatColorFor(ratio, colors.heatmap))
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pluralStringResource(R.plurals.details_last_days, days, days),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.details_pages_short, maxPages),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun QuoteCard(quote: BookQuote) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.stroke, DsShapes.card)
            .padding(Spacing.lg)
    ) {
        Text(
            text = "«${quote.text}»",
            style = MaterialTheme.typography.titleLarge.copy(fontStyle = FontStyle.Italic),
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = stringResource(R.string.details_page, quote.page),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}

private fun heatColorFor(ratio: Float, heatmap: List<androidx.compose.ui.graphics.Color>): androidx.compose.ui.graphics.Color {
    if (heatmap.isEmpty()) return androidx.compose.ui.graphics.Color.Transparent
    // ratio 0..1 → ступень heatmap (пропускаем нулевой «пустой» оттенок).
    val span = heatmap.size - 1
    val index = (1 + ceil(ratio * (span - 1)).toInt()).coerceIn(1, span)
    return heatmap[index]
}

@Composable
private fun readingStatusLabel(status: ReadingStatus): String = when (status) {
    ReadingStatus.None -> stringResource(R.string.details_status_none)
    ReadingStatus.Planned -> stringResource(R.string.details_status_planned)
    ReadingStatus.Reading -> stringResource(R.string.details_status_reading)
    ReadingStatus.Paused -> stringResource(R.string.details_status_paused)
    ReadingStatus.Dropped -> stringResource(R.string.details_status_dropped)
    ReadingStatus.Completed -> stringResource(R.string.details_status_completed)
}

@Composable
private fun paceForecast(currentPage: Int, totalPages: Int): String {
    val remaining = (totalPages - currentPage).coerceAtLeast(0)
    if (remaining == 0) return stringResource(R.string.details_pace_finished)
    // Грубая эвристика темпа: ~25 стр/день. TODO: считать средний темп из сессий.
    val perDay = 25
    val days = ceil(remaining.toFloat() / perDay).toInt().coerceAtLeast(1)
    return pluralStringResource(R.plurals.details_pace_forecast, days, days)
}

/* ---------------------------- Previews ---------------------------- */

private val previewQuotes = listOf(
    BookQuote(text = "Мы все живём в одном мире, но каждый видит свой собственный.", page = 124),
    BookQuote(text = "Тишина бывает разной — есть та, что лечит, и та, что ранит.", page = 318),
)

private val previewSessions = listOf(8, 14, 0, 22, 31, 12, 0, 19, 27, 16)

@Composable
private fun BookDetailsPreviewBody(state: BookDetailsState) {
    Surface(color = BookechiTheme.colors.canvas) {
        BookDetailsScreenV2Content(state = state, onAction = {})
    }
}

@Preview(name = "BookDetails Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun BookDetailsScreenV2PreviewLight() {
    BookechiTheme(darkTheme = false) {
        BookDetailsPreviewBody(
            state = BookDetailsState(
                book = Book(
                    id = 1,
                    name = "Хроники заводной птицы",
                    author = "Харуки Мураками",
                    coverPath = null,
                    pages = 1052,
                    currentPage = 448,
                    readingStatus = ReadingStatus.Reading,
                    isFavorite = true
                ),
                recentSessionPages = previewSessions,
                quotes = previewQuotes,
                rating = 4
            )
        )
    }
}

@Preview(name = "BookDetails Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun BookDetailsScreenV2PreviewDark() {
    BookechiTheme(darkTheme = true) {
        BookDetailsPreviewBody(
            state = BookDetailsState(
                book = Book(
                    id = 1,
                    name = "Хроники заводной птицы",
                    author = "Харуки Мураками",
                    coverPath = null,
                    pages = 1052,
                    currentPage = 448,
                    readingStatus = ReadingStatus.Reading,
                    isFavorite = true
                ),
                recentSessionPages = previewSessions,
                quotes = previewQuotes,
                rating = 4
            )
        )
    }
}

@Preview(name = "BookDetails Empty quotes Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun BookDetailsScreenV2EmptyPreview() {
    BookechiTheme(darkTheme = false) {
        BookDetailsPreviewBody(
            state = BookDetailsState(
                book = Book(
                    id = 3,
                    name = "Великий Гэтсби",
                    author = "Ф. Скотт Фицджеральд",
                    coverPath = null,
                    pages = 180,
                    currentPage = 0,
                    readingStatus = ReadingStatus.Planned,
                    isFavorite = false
                ),
                recentSessionPages = emptyList(),
                quotes = emptyList(),
                rating = 0
            )
        )
    }
}
