package fuwafuwa.time.bookechi.ui.feature.productivity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.BuildConfig
import fuwafuwa.time.bookechi.base.ui.ds.InsightPlinth
import fuwafuwa.time.bookechi.base.ui.ds.MetricCard
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.WeeklyGoalCard
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ActivityChartTab
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityAction
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityViewModel
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart.ActivityPanel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LocalBottomBarHeight
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data object ProductivityScreen : Screen

@Composable
fun ProductivityScreen(
    viewModel: ProductivityViewModel
) {
    val state by viewModel.model.state.collectAsState()

    ProductivityScreenPrivate(
        state = state,
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

        MetricsGrid(state)

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
            text = "Статистика чтения",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
        Text(
            text = "Продуктивность",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary,
        )
    }
}

@Composable
private fun MetricsGrid(state: ProductivityState) {
    val empty = state.isEmpty
    val placeholder = "—"

    val books = if (empty) placeholder else state.booksRead.toString()
    val pages = if (empty) placeholder else formatThousands(state.pagesRead)
    val streak = if (empty) placeholder else state.dayStreak.toString()
    val average = if (empty) placeholder else formatAverage(state.averagePages)

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            MetricCard(
                value = books,
                label = "книг прочитано",
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            MetricCard(
                value = pages,
                label = "страниц прочитано",
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            MetricCard(
                value = streak,
                label = "дней без перерывов",
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            MetricCard(
                value = average,
                label = "стр. в день в среднем",
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun Insight(state: ProductivityState) {
    if (state.dayStreak <= 0) return
    InsightPlinth(text = "Личный рекорд серии: ${state.dayStreak} дней")
}

/** Форматирует число с пробелом как разделителем тысяч: 3480 -> «3 480». */
private fun formatThousands(value: Int): String {
    val sign = if (value < 0) "-" else ""
    val digits = kotlin.math.abs(value).toString()
    val chunked = digits.reversed().chunked(3).joinToString(" ").reversed()
    return sign + chunked
}

/** Среднее с одной цифрой после запятой: 12.5 -> «12,5». */
private fun formatAverage(value: Float): String {
    return String.format("%.1f", value).replace('.', ',')
}

private fun russianMonthName(month: Int): String = when (month) {
    1 -> "Январь"
    2 -> "Февраль"
    3 -> "Март"
    4 -> "Апрель"
    5 -> "Май"
    6 -> "Июнь"
    7 -> "Июль"
    8 -> "Август"
    9 -> "Сентябрь"
    10 -> "Октябрь"
    11 -> "Ноябрь"
    12 -> "Декабрь"
    else -> ""
}

/** «Июнь 2026». Если месяц вне диапазона — только год. */
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
    booksRead = 6,
    pagesRead = 3480,
    dayStreak = 12,
    averagePages = 12.5f,
    weeklyPagesRead = 340,
    weeklyPagesTarget = 400,
    sessions = ProductivityPreviewData.generateMonthData(2026, 6),
    currentYear = 2026,
    currentMonth = 6,
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
