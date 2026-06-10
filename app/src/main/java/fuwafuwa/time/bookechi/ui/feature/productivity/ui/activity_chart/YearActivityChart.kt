package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ActivityIntensity
import fuwafuwa.time.bookechi.base.ui.chart.getRelativeActivityIntensity
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import java.time.LocalDate

private const val MONTHS_IN_YEAR = 12
private const val YEAR_GRID_COLUMNS = 4

private val MONTH_SHORT_LABELS = listOf(
    "Янв", "Фев", "Мар", "Апр",
    "Май", "Июн", "Июл", "Авг",
    "Сен", "Окт", "Ноя", "Дек",
)

private data class MonthBlock(
    val month: Int,
    val intensity: ActivityIntensity,
    val pagesRead: Int,
)

/**
 * Годовой график активности — 12 блоков-месяцев (макет «06 Продуктивность — год»).
 *
 * Сетка 4×3: Янв Фев Мар Апр / Май Июн Июл Авг / Сен Окт Ноя Дек.
 * Каждый блок — скруглённый прямоугольник heatmap[intensity месяца]
 * (интенсивность = суммарные страницы месяца). Месяц без чтения — heatmap[0] с границей.
 * Текущий месяц — обводка accent. Под блоком — короткая подпись месяца.
 */
@Composable
fun YearActivityChart(
    year: Int,
    modifier: Modifier = Modifier,
    config: ActivityChartConfig = ActivityChartConfig(cornerRadius = 10.dp),
    sessions: List<DailyReadingStats> = emptyList(),
) {
    val blocks = remember(year, sessions) { buildMonthBlocks(year, sessions) }
    val today = LocalDate.now()
    val highlightMonth = if (today.year == year) today.monthValue else 0

    val rows = MONTHS_IN_YEAR / YEAR_GRID_COLUMNS

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                for (col in 0 until YEAR_GRID_COLUMNS) {
                    val index = row * YEAR_GRID_COLUMNS + col
                    MonthBlockCell(
                        block = blocks[index],
                        config = config,
                        isCurrentMonth = blocks[index].month == highlightMonth,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthBlockCell(
    block: MonthBlock,
    config: ActivityChartConfig,
    isCurrentMonth: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val shape = RoundedCornerShape(config.cornerRadius)
    val color = colors.heatmap[block.intensity.ordinal]

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(shape)
                .background(color)
                .then(
                    when {
                        isCurrentMonth -> Modifier.border(
                            BorderStroke(1.5.dp, colors.accent),
                            shape,
                        )
                        block.intensity == ActivityIntensity.NONE -> Modifier.border(
                            BorderStroke(1.dp, colors.heatZeroStroke),
                            shape,
                        )
                        else -> Modifier
                    }
                )
        )
        Text(
            text = MONTH_SHORT_LABELS[block.month - 1],
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

/** Суммарные страницы по каждому месяцу, замапленные в интенсивность относительно максимума. */
private fun buildMonthBlocks(
    year: Int,
    sessions: List<DailyReadingStats>,
): List<MonthBlock> {
    val pagesByMonth = IntArray(MONTHS_IN_YEAR)
    sessions.forEach { session ->
        val date = session.localDate
        if (date.year == year) {
            pagesByMonth[date.monthValue - 1] += session.totalPagesRead
        }
    }

    val maxPages = pagesByMonth.maxOrNull() ?: 0

    return (1..MONTHS_IN_YEAR).map { month ->
        val pages = pagesByMonth[month - 1]
        MonthBlock(
            month = month,
            intensity = getRelativeActivityIntensity(pages, maxPages),
            pagesRead = pages,
        )
    }
}

@Preview(name = "YearActivityChart Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun YearActivityChartPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                YearActivityChart(
                    year = 2026,
                    sessions = ProductivityPreviewData.generateYearData(year = 2026),
                )
            }
        }
    }
}

@Preview(name = "YearActivityChart Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun YearActivityChartPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                YearActivityChart(
                    year = 2026,
                    sessions = ProductivityPreviewData.generateYearData(year = 2026),
                )
            }
        }
    }
}
