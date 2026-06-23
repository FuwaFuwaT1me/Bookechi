package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.MinutesRuler
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressAction
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.coroutines.flow.distinctUntilChanged
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

    Box(modifier = modifier.fillMaxSize().background(colors.canvas)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xxl)
            .padding(top = Spacing.xxl, bottom = Spacing.xxl),
    ) {
        BackButton(onClick = { onAction(UpdateProgressAction.NavigateBack) })

        Spacer(Modifier.height(Spacing.xl))

        ProgressHeader(
            book = state.book,
            inputPages = state.updatedInputPages,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.xxxl))

        Text(
            text = stringResource(R.string.update_progress_title),
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textPrimary,
        )

        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = stringResource(R.string.update_progress_subtitle),
            style = MaterialTheme.typography.bodySmall,
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
                text = pluralStringResource(
                    R.plurals.update_book_total_pages,
                    state.book.pages,
                    state.book.pages,
                ),
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

        ReadingTimeCard(
            minutes = state.readingTimeMinutes,
            onClick = { onAction(UpdateProgressAction.OpenReadingTimeSheet) },
        )

        Spacer(Modifier.height(Spacing.xxxl))

        PrimaryButton(
            text = stringResource(R.string.update_save_progress),
            enabled = isValidProgress(state) && !state.isSaving,
            onClick = { onAction(UpdateProgressAction.SaveChanges(state.updatedInputPages)) },
        )
    }

        ReadingTimeSheet(state = state, onAction = onAction)
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
            contentDescription = stringResource(R.string.update_back),
            tint = colors.textPrimary,
            modifier = Modifier.size(18.dp),
        )
    }
}

/**
 * Центрированная шапка: крупная обложка с заливкой прогресса, ниже название и
 * строка с сохранённым прогрессом «сейчас — стр. X / Y · Z%».
 */
@Composable
private fun ProgressHeader(
    book: Book,
    inputPages: Int,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val savedPercent = if (book.pages > 0) (book.currentPage * 100) / book.pages else 0
    val fraction = if (book.pages > 0) inputPages.toFloat() / book.pages.toFloat() else 0f
    // Старт сессии — сохранённая страница книги (откуда пользователь начал читать).
    val startFraction = if (book.pages > 0) book.currentPage.toFloat() / book.pages.toFloat() else 0f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProgressCover(book = book, fraction = fraction, startFraction = startFraction)

        Spacer(Modifier.height(Spacing.lg))

        Text(
            text = book.name,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = stringResource(
                R.string.update_now_page_of,
                book.currentPage,
                book.pages,
                savedPercent,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

private val CoverWidth = 124.dp

/**
 * Обложка с «заливкой прогресса»: вся обложка обесцвечена и притушена (ч/б,
 * пониженная непрозрачность), а прочитанная часть снизу до [fraction] показана в
 * полном цвете. На границе — линия и бейдж с процентом. Граница анимируется.
 */
@Composable
private fun ProgressCover(
    book: Book,
    fraction: Float,
    startFraction: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val coverHeight = CoverWidth * 1.5f
    val animFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "coverFill",
    )
    // Палитра ч/б + затемнение для непрочитанной части (живёт всё время, статична).
    val dimPaint = remember {
        Paint().apply {
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
            alpha = 0.45f
        }
    }
    val percent = (animFraction * 100f).roundToInt()
    val hasPages = book.pages > 0

    Box(
        modifier = modifier.size(width = CoverWidth, height = coverHeight),
    ) {
        // Внутренний слой обрезается формой обложки (заливка/ч-б/линия не вылезают
        // за скругления). Бейдж рисуем снаружи — он может выходить за край.
        Box(modifier = Modifier.matchParentSize().clip(DsShapes.cover)) {
            if (hasPages) {
                // База: обесцвеченная и тусклая обложка целиком.
                BookCover(
                    coverPath = book.coverPath,
                    title = book.name,
                    author = book.author,
                    width = CoverWidth,
                    modifier = Modifier.drawWithContent {
                        drawIntoCanvas { canvas ->
                            canvas.saveLayer(Rect(Offset.Zero, size), dimPaint)
                            drawContent()
                            canvas.restore()
                        }
                    },
                )
                // Полноцветная прочитанная часть снизу до animFraction.
                BookCover(
                    coverPath = book.coverPath,
                    title = book.name,
                    author = book.author,
                    width = CoverWidth,
                    modifier = Modifier.drawWithContent {
                        clipRect(top = size.height * (1f - animFraction)) {
                            this@drawWithContent.drawContent()
                        }
                    },
                )
                // Черточка — откуда пользователь начал читать (старт сессии).
                // Пунктир, чтобы отличать от сплошной линии текущей заливки.
                if (startFraction > 0.001f && startFraction < animFraction - 0.005f) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val y = size.height * (1f - startFraction)
                        drawLine(
                            color = Color.White.copy(alpha = 0.9f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(5.dp.toPx(), 4.dp.toPx()),
                            ),
                        )
                    }
                }
                // Линия-граница заливки.
                if (animFraction > 0.001f && animFraction < 0.999f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .offset(y = coverHeight * (1f - animFraction) - 1.dp)
                            .background(colors.accent),
                    )
                }
            } else {
                // Нет данных о страницах — просто обложка без заливки.
                BookCover(
                    coverPath = book.coverPath,
                    title = book.name,
                    author = book.author,
                    width = CoverWidth,
                )
            }
        }

        // Подпись «начало» у черточки старта — вне клипа, слева (чтобы не
        // пересекаться с центральным бейджем процента).
        if (hasPages && startFraction > 0.001f && startFraction < animFraction - 0.005f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    // Выносим левее края обложки — чтобы не пересекаться с
                    // центральным бейджем процента на близких уровнях.
                    .offset(x = (-22).dp, y = coverHeight * (1f - startFraction) - 9.dp)
                    .height(18.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(horizontal = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.update_start_marker),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.accentDeep,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Бейдж с процентом — поверх границы, вне клипа (не обрезается на краях).
        // Показываем и на 100% (когда вся обложка залита).
        if (hasPages && animFraction > 0.001f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = coverHeight * (1f - animFraction) - 11.dp)
                    .height(22.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
                    .padding(horizontal = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
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
    // Цвета как в дизайне: число — эспрессо (textPrimary), при превышении — терракота (accentDeep).
    val isError = value > total
    val numberColor = if (isError) colors.accentDeep else colors.textPrimary
    val underlineColor = if (isError) colors.accentDeep else colors.textSecondary.copy(alpha = 0.45f)

    val numberStyle = MaterialTheme.typography.displayLarge.copy(
        fontSize = 56.sp,
        lineHeight = 60.sp,
    )

    // Локальный TextFieldValue — сохраняет позицию курсора. Внешнее значение
    // (пресеты +10, слайдер) принимаем, только если оно пришло не «эхом» нашего
    // ввода, иначе асинхронный StateFlow сбрасывает курсор (170 → 107).
    var fieldValue by remember {
        mutableStateOf(
            (if (value == 0) "" else value.toString()).let { TextFieldValue(it, TextRange(it.length)) }
        )
    }
    var lastEmitted by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        if (value != lastEmitted) {
            val d = if (value == 0) "" else value.toString()
            fieldValue = TextFieldValue(d, TextRange(d.length))
            lastEmitted = value
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        // Поле ввода во всю ширину: число слева, поле занимает всё свободное место,
        // подчёркивание (drawBehind) тянется по всей ширине поля, обрываясь до «/ 320».
        BasicTextField(
            value = fieldValue,
            onValueChange = { new ->
                val digits = new.text.filter { it.isDigit() }.take(6)
                val sel = minOf(new.selection.end, digits.length)
                fieldValue = TextFieldValue(digits, TextRange(sel))
                val intValue = digits.toIntOrNull() ?: 0
                if (intValue != lastEmitted) {
                    lastEmitted = intValue
                    onValueChange(intValue)
                }
            },
            textStyle = numberStyle.copy(color = numberColor),
            singleLine = true,
            cursorBrush = SolidColor(colors.accent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .weight(1f)
                .drawBehind {
                    val y = size.height - 1.dp.toPx()
                    drawLine(
                        color = underlineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 2.dp.toPx(),
                    )
                }
                .padding(bottom = Spacing.sm),
            decorationBox = { inner ->
                if (fieldValue.text.isEmpty()) {
                    Text(
                        text = "0",
                        style = numberStyle,
                        color = colors.textSecondary.copy(alpha = 0.35f),
                    )
                }
                inner()
            },
        )
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = stringResource(R.string.update_slash_total, total),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = Spacing.md),
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

    val thumbRadius = 10.dp
    val trackHeight = 8.dp
    val trackColor = colors.stroke
    val activeColor = colors.accent
    val thumbColor = colors.accentDeep

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
                val cx = left + usable * fraction
                // неактивный трек на всю ширину (вровень с подписями и подчёркиванием числа)
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(0f, cy - th / 2f),
                    size = Size(size.width, th),
                    cornerRadius = corner,
                )
                // активная часть от левого края до точки
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(0f, cy - th / 2f),
                    size = Size(cx, th),
                    cornerRadius = corner,
                )
                // thumb: терракотовая точка с белой обводкой и мягкой тенью
                val borderPx = 4.dp.toPx()
                // мягкая тень — радиальный градиент чуть ниже точки
                val shadowCenter = Offset(cx, cy + 2.dp.toPx())
                val shadowRadius = r + borderPx + 6.dp.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent),
                        center = shadowCenter,
                        radius = shadowRadius,
                    ),
                    radius = shadowRadius,
                    center = shadowCenter,
                )
                drawCircle(color = Color.White, radius = r + borderPx, center = Offset(cx, cy))
                drawCircle(color = thumbColor, radius = r, center = Offset(cx, cy))
            }
        }

        Spacer(Modifier.height(Spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.update_page_short, startPages),
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

@Composable
private fun ReadTodayCounter(
    startPages: Int,
    updatedInputPages: Int,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val read = (updatedInputPages - startPages).coerceAtLeast(0)
    val prefix = stringResource(R.string.update_today_read_prefix)
    val readPhrase = pluralStringResource(R.plurals.update_read_today_pages, read, read)

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append(prefix)
            withStyle(
                SpanStyle(color = colors.accent, fontWeight = FontWeight.Bold),
            ) {
                append(readPhrase)
            }
        },
        style = MaterialTheme.typography.bodyLarge,
        color = colors.textPrimary,
    )
}

@Composable
private fun readingTimeLabel(minutes: Int): String = when {
    minutes <= 0 -> stringResource(R.string.update_time_unset)
    minutes < 60 -> stringResource(R.string.update_time_minutes, minutes)
    minutes % 60 == 0 -> stringResource(R.string.update_time_hours, minutes / 60)
    else -> stringResource(R.string.update_time_hours_minutes, minutes / 60, minutes % 60)
}

/** Карточка «Время чтения» на экране: открывает лист с линейкой. */
@Composable
private fun ReadingTimeCard(
    minutes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surface)
            .border(1.dp, colors.stroke, DsShapes.card)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.accentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.size(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.update_time_card_label),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = readingTimeLabel(minutes),
                style = MaterialTheme.typography.titleLarge,
                color = if (minutes > 0) colors.accentDeep else colors.textSecondary,
            )
        }
        Text(
            text = stringResource(R.string.update_time_change),
            style = MaterialTheme.typography.labelLarge,
            color = colors.accent,
        )
    }
}

/** Лист «Сколько читали?» с линейкой-пикером минут. Высота — по контенту (wrap). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReadingTimeSheet(
    state: UpdateProgressState,
    onAction: (UpdateProgressAction) -> Unit,
) {
    if (!state.isReadingTimeSheetOpen) return
    val colors = BookechiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draft by remember { mutableIntStateOf(state.readingTimeMinutes) }

    ModalBottomSheet(
        onDismissRequest = { onAction(UpdateProgressAction.CloseReadingTimeSheet) },
        sheetState = sheetState,
        containerColor = colors.canvas,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.update_time_question),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.update_time_hint),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = readingTimeLabel(draft),
                style = MaterialTheme.typography.displaySmall,
                color = if (draft > 0) colors.accent else colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.lg))
            MinutesRuler(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.xl))
            PrimaryButton(
                text = stringResource(R.string.lib_save),
                onClick = {
                    onAction(UpdateProgressAction.UpdateReadingTime(draft))
                    onAction(UpdateProgressAction.CloseReadingTimeSheet)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
