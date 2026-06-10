package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.WarmTextField
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressAction
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class UpdateProgressScreen(
    val book: Book
) : Screen

@Composable
fun UpdateProgressScreen(
    viewModel: UpdateProgressViewModel,
) {
    val state by viewModel.model.state.collectAsState()

    UpdateProgressScreenContent(
        state = state,
        onAction = viewModel::sendAction
    )
}

private fun isValidProgress(state: UpdateProgressState): Boolean =
    state.startPages < state.updatedInputPages && state.updatedInputPages <= state.book.pages

@Composable
private fun UpdateProgressScreenContent(
    state: UpdateProgressState,
    onAction: (UpdateProgressAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BookechiTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xxl)
            .padding(top = Spacing.xxl, bottom = Spacing.xxl),
    ) {
        BackButton(onClick = { onAction(UpdateProgressAction.NavigateBack) })

        Spacer(Modifier.height(Spacing.xl))

        BookMiniInfo(book = state.book)

        Spacer(Modifier.height(Spacing.xxxl))

        Text(
            text = "Где остановились сегодня?",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Дочитал до страницы",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )

        Spacer(Modifier.height(Spacing.lg))

        PageCounter(
            value = state.updatedInputPages,
            total = state.book.pages,
            onValueChange = { onAction(UpdateProgressAction.UpdatePageInput(it)) },
        )

        val overLimit = state.updatedInputPages > state.book.pages
        if (overLimit) {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "В книге всего ${state.book.pages} страниц.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.accentDeep,
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        ProgressSlider(
            startPages = state.startPages,
            total = state.book.pages,
            value = state.updatedInputPages,
            onValueChange = { onAction(UpdateProgressAction.UpdatePageInput(it)) },
        )

        Spacer(Modifier.height(Spacing.xl))

        ReadTodayCounter(
            startPages = state.startPages,
            updatedInputPages = state.updatedInputPages,
        )

        Spacer(Modifier.height(Spacing.xxxl))

        ReadingTimeField(
            value = state.readingTimeMinutes,
            onValueChange = { onAction(UpdateProgressAction.UpdateReadingTime(it)) },
        )

        Spacer(Modifier.height(Spacing.xxxl))

        PrimaryButton(
            text = "Сохранить прогресс",
            enabled = isValidProgress(state) && !state.isSaving,
            onClick = { onAction(UpdateProgressAction.SaveChanges(state.updatedInputPages)) },
        )
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .border(1.dp, colors.stroke, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Назад",
            tint = colors.textPrimary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun BookMiniInfo(
    book: Book,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val percent = if (book.pages > 0) (book.currentPage * 100) / book.pages else 0

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BookCover(
            coverPath = book.coverPath,
            title = book.name,
            author = book.author,
            width = 64.dp,
        )

        Spacer(Modifier.size(Spacing.lg))

        Column {
            Text(
                text = book.name,
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "сейчас — стр. ${book.currentPage} / ${book.pages} · $percent%",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun PageCounter(
    value: Int,
    total: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val underlineColor = colors.accentDeep
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        // Крупное число — редактируемое: можно ввести страницу с клавиатуры.
        BasicTextField(
            value = if (value == 0) "" else value.toString(),
            onValueChange = { text ->
                val digits = text.filter { it.isDigit() }.take(6)
                onValueChange(digits.toIntOrNull() ?: 0)
            },
            textStyle = MaterialTheme.typography.displayLarge.copy(color = colors.accentDeep),
            singleLine = true,
            cursorBrush = SolidColor(colors.accent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .widthIn(min = 96.dp)
                .width(IntrinsicSize.Min)
                .drawBehind {
                    val y = size.height - 1.dp.toPx()
                    drawLine(
                        color = underlineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 2.dp.toPx(),
                    )
                }
                .padding(bottom = Spacing.xs),
            decorationBox = { inner ->
                if (value == 0) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.displayLarge,
                        color = colors.textSecondary.copy(alpha = 0.4f),
                    )
                }
                inner()
            },
        )
        Spacer(Modifier.size(Spacing.sm))
        Text(
            text = "/ $total",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )
    }
}

@Composable
private fun ProgressSlider(
    startPages: Int,
    total: Int,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    // Гарантируем валидный диапазон (start < end).
    val lower = startPages.coerceAtMost(total - 1).coerceAtLeast(0)
    val upper = total.coerceAtLeast(lower + 1)
    val fraction = (value.coerceIn(lower, upper) - lower).toFloat() / (upper - lower).toFloat()

    val thumbRadius = 12.dp
    val trackHeight = 8.dp
    val trackColor = colors.stroke
    val activeColor = colors.accent
    val haloColor = colors.surfaceElevated

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .pointerInput(lower, upper) {
                    val r = thumbRadius.toPx()
                    detectTapGestures { pos ->
                        val usable = (size.width - 2 * r).coerceAtLeast(1f)
                        val f = ((pos.x - r) / usable).coerceIn(0f, 1f)
                        onValueChange((lower + f * (upper - lower)).roundToInt())
                    }
                }
                .pointerInput(lower, upper) {
                    val r = thumbRadius.toPx()
                    detectHorizontalDragGestures { change, _ ->
                        val usable = (size.width - 2 * r).coerceAtLeast(1f)
                        val f = ((change.position.x - r) / usable).coerceIn(0f, 1f)
                        onValueChange((lower + f * (upper - lower)).roundToInt())
                        change.consume()
                    }
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cy = size.height / 2f
                val r = thumbRadius.toPx()
                val left = r
                val usable = (size.width - 2 * r).coerceAtLeast(1f)
                val th = trackHeight.toPx()
                val corner = CornerRadius(th / 2f, th / 2f)
                // неактивный трек
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(left, cy - th / 2f),
                    size = Size(usable, th),
                    cornerRadius = corner,
                )
                // активная часть
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(left, cy - th / 2f),
                    size = Size(usable * fraction, th),
                    cornerRadius = corner,
                )
                // thumb: тёплое гало + сплошной акцент
                val cx = left + usable * fraction
                drawCircle(color = haloColor, radius = r, center = Offset(cx, cy))
                drawCircle(color = activeColor, radius = r - 3.dp.toPx(), center = Offset(cx, cy))
            }
        }

        Spacer(Modifier.height(Spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "стр. $startPages",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            Text(
                text = "$total",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

private fun pagesPlural(count: Int): String {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..14 -> "страниц"
        mod10 == 1 -> "страницу"
        mod10 in 2..4 -> "страницы"
        else -> "страниц"
    }
}

@Composable
private fun ReadTodayCounter(
    startPages: Int,
    updatedInputPages: Int,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val read = (updatedInputPages - startPages).coerceAtLeast(0)

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append("Прочитано сегодня: ")
            withStyle(
                SpanStyle(color = colors.accent, fontWeight = FontWeight.Bold),
            ) {
                append("+$read")
            }
            append(" ${pagesPlural(read)}")
        },
        style = MaterialTheme.typography.bodyLarge,
        color = colors.textPrimary,
    )
}

@Composable
private fun ReadingTimeField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Время чтения, мин — необязательно",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(Spacing.sm))
        WarmTextField(
            value = value.takeIf { it > 0 }?.toString() ?: "",
            onValueChange = { text ->
                onValueChange(text.filter { it.isDigit() }.toIntOrNull() ?: 0)
            },
            label = "Минуты",
            placeholder = "25",
            keyboardType = KeyboardType.Number,
        )
    }
}

/* ---------------------------------------------------------------------------
 * Previews
 * ------------------------------------------------------------------------- */

private val previewBook = Book(
    name = "Норвежский лес",
    author = "Харуки Мураками",
    coverPath = null,
    pages = 256,
    currentPage = 54,
    isFavorite = false,
)

@Preview(name = "Valid Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun ValidProgressPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateProgressScreenContent(
                state = UpdateProgressState(
                    book = previewBook,
                    startPages = 54,
                    updatedInputPages = 86,
                    readingTimeMinutes = 25,
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "Valid Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun ValidProgressPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateProgressScreenContent(
                state = UpdateProgressState(
                    book = previewBook,
                    startPages = 54,
                    updatedInputPages = 86,
                    readingTimeMinutes = 25,
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "Error Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun ErrorProgressPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateProgressScreenContent(
                state = UpdateProgressState(
                    book = previewBook,
                    startPages = 54,
                    updatedInputPages = 300,
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "Error Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun ErrorProgressPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateProgressScreenContent(
                state = UpdateProgressState(
                    book = previewBook,
                    startPages = 54,
                    updatedInputPages = 300,
                ),
                onAction = {},
            )
        }
    }
}
