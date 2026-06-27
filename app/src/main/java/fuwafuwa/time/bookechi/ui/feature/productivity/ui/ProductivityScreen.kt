package fuwafuwa.time.bookechi.ui.feature.productivity.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import fuwafuwa.time.bookechi.BuildConfig
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.coverTintFor
import fuwafuwa.time.bookechi.base.ui.ds.InsightPlinth
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.WeeklyGoalCard
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ActivityChartTab
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityAction
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ShelfCover
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityViewModel
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart.ActivityPanel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LocalBottomBarHeight
import kotlinx.serialization.Serializable
import java.time.LocalDate
import kotlin.math.roundToInt

@Serializable
data object ProductivityScreen : Screen

@Composable
fun ProductivityScreen(
    viewModel: ProductivityViewModel
) {
    val state by viewModel.model.state.collectAsState()

    ProductivityScreenPrivate(
        state = state,
        onOpenJournal = { viewModel.sendAction(ProductivityAction.OpenReadingLog) },
        onOpenShelf = { viewModel.sendAction(ProductivityAction.OpenReadShelf) },
        onToggleActivityChartSwitch = { tab ->
            ActivityChartTab.fromIndex(tab)?.let {
                viewModel.sendAction(
                    ProductivityAction.ToggleActivityChartTab(it)
                )
            }
        },
        debugActions = if (BuildConfig.DEBUG && false) {
            ProductivityDebugActions(
                overwriteYear = { year, pagesPerDay, booksCount ->
                    viewModel.sendAction(
                        ProductivityAction.DebugOverwriteYear(year, pagesPerDay, booksCount)
                    )
                },
                overwriteMonth = { year, month, pagesPerDay, booksCount ->
                    viewModel.sendAction(
                        ProductivityAction.DebugOverwriteMonth(year, month, pagesPerDay, booksCount)
                    )
                },
                fillRecentWeeks = { pagesPerDay, booksCount ->
                    viewModel.sendAction(
                        ProductivityAction.DebugFillRecentWeeks(pagesPerDay, booksCount)
                    )
                },
                clearAll = {
                    viewModel.sendAction(ProductivityAction.DebugClearAll)
                }
            )
        } else {
            null
        }
    )
}

private data class ProductivityDebugActions(
    val overwriteYear: (Int, Int, Int) -> Unit,
    val overwriteMonth: (Int, Int, Int, Int) -> Unit,
    val fillRecentWeeks: (Int, Int) -> Unit,
    val clearAll: () -> Unit
)

@Composable
private fun ProductivityScreenPrivate(
    state: ProductivityState,
    onOpenJournal: () -> Unit = {},
    onOpenShelf: () -> Unit = {},
    onToggleActivityChartSwitch: (Int) -> Unit,
    debugActions: ProductivityDebugActions? = null
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {

        Spacer(modifier = Modifier.height(Spacing.lg))

        Header()

        // A. Карточки-входы: полка и журнал (или пустые состояния).
        if (state.shelfEmpty) {
            ShelfEmptyState()
        } else {
            ShelfEntryCard(state = state, onClick = onOpenShelf)
        }

        if (state.journalEmpty) {
            JournalEmptyState()
        } else {
            JournalEntryCard(state = state, onClick = onOpenJournal)
        }

        // B. Недельная цель + инсайт «личный рекорд серии».
        WeeklyGoalCard(
            pagesRead = state.weeklyPagesRead,
            pagesTarget = state.weeklyPagesTarget,
        )

        Insight(state)

        ActivityPanel(state, onToggleActivityChartSwitch)

        if (debugActions != null) {
            DebugPanel(
                onOverwriteYear = debugActions.overwriteYear,
                onOverwriteMonth = debugActions.overwriteMonth,
                onFillRecentWeeks = debugActions.fillRecentWeeks,
                onClearAll = debugActions.clearAll
            )
        }

        Spacer(Modifier.height(LocalBottomBarHeight.current + Spacing.lg))
    }
}

@Composable
private fun Header() {
    val colors = BookechiTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = stringResource(R.string.prod_header_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
        Text(
            text = stringResource(R.string.prod_header_title),
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary,
        )
    }
}

/* ---- Карточки-входы (полка / журнал) ------------------------------------- */

/** Общая оболочка кликабельной строки-карточки: превью · 2 строки текста · шеврон. */
@Composable
private fun EntryCardShell(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    preview: @Composable () -> Unit,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.stroke, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp).size(width = 64.dp, height = 56.dp),
            contentAlignment = Alignment.Center,
        ) { preview() }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun ShelfEntryCard(state: ProductivityState, onClick: () -> Unit) {
    val booksWord = pluralStringResource(R.plurals.shelf_books_word, state.booksRead)
    val pagesWord = pluralStringResource(R.plurals.stat_pages_word, state.shelfPagesTotal)
    val subtitle = "${formatThousands(state.booksRead)} $booksWord · " +
        "${formatThousands(state.shelfPagesTotal)} $pagesWord"
    EntryCardShell(
        title = stringResource(R.string.stat_shelf_card_title),
        subtitle = subtitle,
        onClick = onClick,
        preview = { ShelfFanPreview(covers = state.shelfCovers) },
    )
}

@Composable
private fun JournalEntryCard(state: ProductivityState, onClick: () -> Unit) {
    val streakPart = pluralStringResource(R.plurals.shelf_days, state.dayStreak, state.dayStreak)
    EntryCardShell(
        title = stringResource(R.string.prod_journal_card_title),
        subtitle = stringResource(
            R.string.stat_journal_subtitle,
            streakPart,
            state.averagePages.roundToInt(),
        ),
        onClick = onClick,
        preview = { JournalSparkPreview(values = state.journalSpark) },
    )
}

/**
 * Веер из ≤3 обложек последних прочитанных книг: книги расходятся от общего
 * основания (низ-центр), центральная перекрывает боковые. Габарит ровно 70×56.
 */
@Composable
private fun ShelfFanPreview(covers: List<ShelfCover>) {
    val items = covers.take(3)
    val rot = listOf(-16f, 0f, 16f)
    val tx = listOf(-17f, 0f, 17f)
    Box(modifier = Modifier.size(width = 70.dp, height = 56.dp)) {
        if (items.isEmpty()) {
            // Пустой веер — три пунктирные заготовки на тех же позициях.
            listOf(0, 1, 2).forEach { p -> FanCover(rot = rot[p], tx = tx[p], top = p == 1, baseColor = null) }
        } else {
            // Для 1–2 книг держим центр заполненным.
            val positions = when (items.size) {
                1 -> listOf(1)
                2 -> listOf(0, 1)
                else -> listOf(0, 1, 2)
            }
            items.forEachIndexed { idx, cover ->
                val p = positions[idx]
                FanCover(rot = rot[p], tx = tx[p], top = p == 1, baseColor = coverTintFor(cover.title))
            }
        }
    }
}

/** Одна обложка веера: 33×48, скруглён правый «обрез», корешок слева, тени. */
@Composable
private fun BoxScope.FanCover(rot: Float, tx: Float, top: Boolean, baseColor: Color?) {
    val colors = BookechiTheme.colors
    // border-radius 3 5 5 3 — правый край (передний обрез книги) скруглён сильнее.
    val shape = RoundedCornerShape(topStart = 3.dp, topEnd = 5.dp, bottomEnd = 5.dp, bottomStart = 3.dp)
    val base = Modifier
        .align(Alignment.BottomCenter)
        .zIndex(if (top) 3f else 1f)
        .graphicsLayer {
            translationX = tx.dp.toPx()
            rotationZ = rot
            transformOrigin = TransformOrigin(0.5f, 1f)
        }
        .size(width = 33.dp, height = 48.dp)

    if (baseColor == null) {
        // Пустая заготовка: заливка card-tint + пунктирный контур.
        Box(
            modifier = base.drawBehind {
                val r = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                drawRoundRect(color = colors.cardTint, cornerRadius = r)
                drawRoundRect(
                    color = colors.divider,
                    cornerRadius = r,
                    style = Stroke(
                        width = 1.4.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())),
                    ),
                )
            },
        )
        return
    }

    Box(
        modifier = base
            // Контактная тень под книгой.
            .shadow(6.dp, shape, clip = false)
            .clip(shape)
            // Вертикальный градиент: базовый цвет → затемнённый.
            .background(
                Brush.verticalGradient(listOf(baseColor, lerp(baseColor, Color.Black, 0.28f))),
            ),
    ) {
        // Тень переднего обреза справа (inset -2 0 4 rgba(0,0,0,.22)).
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(6.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f)))),
        )
        // Блик корешка слева (inset 2 0 0 rgba(255,255,255,.16)).
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(2.dp)
                .background(Color.White.copy(alpha = 0.16f)),
        )
        // Тонкая линия корешка (::after): x=3, инсет 4 сверху/снизу.
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 3.dp)
                .width(1.5.dp)
                .fillMaxHeight(40f / 48f)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.20f)),
        )
    }
}

/** Мини линейный график: восходящая кривая с лёгкой заливкой accentSoft и точкой на конце. */
@Composable
private fun JournalSparkPreview(values: List<Int>) {
    val colors = BookechiTheme.colors
    // Если данных мало — рисуем приятную восходящую кривую по умолчанию.
    val data = if (values.size >= 2) values else listOf(2, 3, 3, 5, 4, 6, 7, 9)
    val maxV = (data.maxOrNull() ?: 1).coerceAtLeast(1).toFloat()
    val fill = colors.accentSoft.copy(alpha = 0.40f)
    val line = colors.textSecondary

    Canvas(modifier = Modifier.size(width = 60.dp, height = 38.dp)) {
        val w = size.width
        val h = size.height
        val padY = 4.dp.toPx()
        val usableH = h - padY * 2
        val n = data.size
        fun pt(i: Int): Offset {
            val x = if (n == 1) w else w * i / (n - 1)
            val y = padY + usableH * (1f - data[i] / maxV)
            return Offset(x, y)
        }

        val linePath = Path().apply {
            moveTo(pt(0).x, pt(0).y)
            for (i in 1 until n) lineTo(pt(i).x, pt(i).y)
        }
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(fillPath, color = fill)
        drawPath(
            linePath,
            color = line,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round,
            ),
        )
        val end = pt(n - 1)
        drawCircle(color = colors.textPrimary, radius = 3.dp.toPx(), center = end)
    }
}

/* ---- Пустые состояния карточек ------------------------------------------- */

@Composable
private fun EmptyStateBlock(
    title: String,
    subtitle: String,
    preview: @Composable () -> Unit,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.stroke, RoundedCornerShape(24.dp))
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(
            modifier = Modifier.size(width = 64.dp, height = 56.dp),
            contentAlignment = Alignment.Center,
        ) { preview() }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun ShelfEmptyState() {
    EmptyStateBlock(
        title = stringResource(R.string.stat_shelf_empty_title),
        subtitle = stringResource(R.string.stat_shelf_empty_subtitle),
        preview = { EmptyShelfLedge() },
    )
}

@Composable
private fun JournalEmptyState() {
    val colors = BookechiTheme.colors
    EmptyStateBlock(
        title = stringResource(R.string.stat_journal_empty_title),
        subtitle = stringResource(R.string.stat_journal_empty_subtitle),
        preview = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.accentSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = colors.accentDeep,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    )
}

/** Иллюстрация пустого леджа: 3 контурных пунктирных слота на «полке». */
@Composable
private fun EmptyShelfLedge() {
    val colors = BookechiTheme.colors
    val stroke = colors.stroke
    val ledge = colors.textSecondary.copy(alpha = 0.45f)
    Canvas(modifier = Modifier.size(width = 60.dp, height = 50.dp)) {
        val w = size.width
        val slotW = 13.dp.toPx()
        val slotH = 30.dp.toPx()
        val gap = (w - slotW * 3) / 4f
        val top = 4.dp.toPx()
        val dash = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
            floatArrayOf(4.dp.toPx(), 3.dp.toPx()),
        )
        for (i in 0 until 3) {
            val x = gap + i * (slotW + gap)
            drawRoundRect(
                color = stroke,
                topLeft = Offset(x, top),
                size = Size(slotW, slotH),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = dash,
                ),
            )
        }
        // «Полка» под слотами.
        drawRoundRect(
            color = ledge,
            topLeft = Offset(0f, top + slotH + 3.dp.toPx()),
            size = Size(w, 3.dp.toPx()),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
        )
    }
}

@Composable
private fun Insight(state: ProductivityState) {
    if (state.dayStreak <= 0) return
    InsightPlinth(
        text = pluralStringResource(
            R.plurals.prod_insight_streak_record,
            state.dayStreak,
            state.dayStreak,
        ),
        backgroundColor = BookechiTheme.colors.accentSoft,
    )
}

/** Форматирует число с пробелом как разделителем тысяч: 3480 -> «3 480». */
private fun formatThousands(value: Int): String {
    val sign = if (value < 0) "-" else ""
    val digits = kotlin.math.abs(value).toString()
    val chunked = digits.reversed().chunked(3).joinToString(" ").reversed()
    return sign + chunked
}

@Composable
private fun russianMonthName(month: Int): String = when (month) {
    1 -> stringResource(R.string.prod_month_january)
    2 -> stringResource(R.string.prod_month_february)
    3 -> stringResource(R.string.prod_month_march)
    4 -> stringResource(R.string.prod_month_april)
    5 -> stringResource(R.string.prod_month_may)
    6 -> stringResource(R.string.prod_month_june)
    7 -> stringResource(R.string.prod_month_july)
    8 -> stringResource(R.string.prod_month_august)
    9 -> stringResource(R.string.prod_month_september)
    10 -> stringResource(R.string.prod_month_october)
    11 -> stringResource(R.string.prod_month_november)
    12 -> stringResource(R.string.prod_month_december)
    else -> ""
}

/** «Июнь 2026». Если месяц вне диапазона — только год. */
@Composable
fun russianMonthYear(month: Int, year: Int): String {
    val name = russianMonthName(month)
    return if (name.isEmpty()) "$year" else "$name $year"
}

@Preview(name = "Productivity Filled Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun PreviewProductivityFilledLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ProductivityScreenPrivate(
                state = filledPreviewState(),
                onToggleActivityChartSwitch = {},
                debugActions = null
            )
        }
    }
}

@Preview(name = "Productivity Filled Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun PreviewProductivityFilledDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            ProductivityScreenPrivate(
                state = filledPreviewState().copy(activityChartTab = ActivityChartTab.YEAR),
                onToggleActivityChartSwitch = {},
                debugActions = null
            )
        }
    }
}

@Preview(name = "Productivity Zero Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun PreviewProductivityZeroLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ProductivityScreenPrivate(
                state = ProductivityState(currentYear = 2026, currentMonth = 6),
                onToggleActivityChartSwitch = {},
                debugActions = null
            )
        }
    }
}

@Preview(name = "Productivity Zero Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun PreviewProductivityZeroDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            ProductivityScreenPrivate(
                state = ProductivityState(currentYear = 2026, currentMonth = 6),
                onToggleActivityChartSwitch = {},
                debugActions = null
            )
        }
    }
}

private fun filledPreviewState() = ProductivityState(
    booksRead = 12,
    pagesRead = 3480,
    dayStreak = 7,
    averagePages = 48f,
    weeklyPagesRead = 340,
    weeklyPagesTarget = 400,
    sessions = ProductivityPreviewData.generateMonthData(2026, 6),
    currentYear = 2026,
    currentMonth = 6,
    shelfCovers = listOf(
        ShelfCover(null, "Норвежский лес", "Харуки Мураками"),
        ShelfCover(null, "1984", "Джордж Оруэлл"),
        ShelfCover(null, "Мастер и Маргарита", "Михаил Булгаков"),
    ),
    shelfPagesTotal = 3480,
    journalSpark = listOf(20, 35, 28, 44, 40, 52, 48, 60),
)

@Composable
private fun DebugPanel(
    onOverwriteYear: (Int, Int, Int) -> Unit,
    onOverwriteMonth: (Int, Int, Int, Int) -> Unit,
    onFillRecentWeeks: (Int, Int) -> Unit,
    onClearAll: () -> Unit
) {
    val today = remember { LocalDate.now() }
    var yearText by remember { mutableStateOf(today.year.toString()) }
    var monthText by remember { mutableStateOf(today.monthValue.toString()) }
    var pagesText by remember { mutableStateOf("30") }
    var booksText by remember { mutableStateOf("3") }

    val year = yearText.toIntOrNull() ?: today.year
    val month = (monthText.toIntOrNull() ?: today.monthValue).coerceIn(1, 12)
    val pages = pagesText.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val books = booksText.toIntOrNull()?.coerceAtLeast(1) ?: 1

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F3F1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Debug panel (overwrite data)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { yearText = it },
                    label = { Text("Year") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = monthText,
                    onValueChange = { monthText = it },
                    label = { Text("Month") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = pagesText,
                    onValueChange = { pagesText = it },
                    label = { Text("Pages/Day") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = booksText,
                    onValueChange = { booksText = it },
                    label = { Text("Books") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onOverwriteYear(year, pages, books) }) {
                    Text("Overwrite Year")
                }
                Button(onClick = { onOverwriteMonth(year, month, pages, books) }) {
                    Text("Overwrite Month")
                }
            }

            Button(onClick = { onFillRecentWeeks(pages, books) }) {
                Text("Fill Recent 3.5 Weeks")
            }

            Button(onClick = onClearAll) {
                Text("Clear Books + Sessions")
            }
        }
    }
}
