package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.time.Date
import fuwafuwa.time.bookechi.base.time.getDaysInMonth
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ChartCellData
import fuwafuwa.time.bookechi.base.ui.chart.getRelativeActivityIntensity
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import java.time.LocalDate

private const val DAYS_IN_WEEK = 7

private val WEEKDAY_LABELS = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

/**
 * Месячный график активности — календарная сетка (макет «05 Продуктивность — месяц»).
 *
 * Шапка-строка дней недели (Пн..Вс) + строки-недели по 7 колонок.
 * День месяца стоит в колонке своего дня недели (понедельник — первый).
 * Пустые позиции до 1-го числа и после последнего — пропуски.
 * Ячейка — скруглённый квадрат heatmap[intensity]; нулевая — heatmap[0] с границей;
 * сегодняшний день — обводка accent.
 */
@Composable
fun MonthActivityChart(
    year: Int,
    month: Int,
    modifier: Modifier = Modifier,
    config: ActivityChartConfig = ActivityChartConfig(cornerRadius = 6.dp),
    sessions: List<DailyReadingStats> = emptyList(),
) {
    val colors = BookechiTheme.colors
    val today = LocalDate.now()

    val weeks = remember(year, month, sessions) {
        val daysInMonth = getDaysInMonth(year, month)
        val maxPages = sessions.maxOfOrNull { it.totalPagesRead } ?: 0
        buildCalendarWeeks(daysInMonth, sessions, maxPages)
    }

    val cellSpacing = if (config.showSpacing) config.itemHorizontalSpacing else 0.dp
    val rowSpacing = if (config.showSpacing) config.itemVerticalSpacing else 0.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(rowSpacing),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(cellSpacing),
        ) {
            WEEKDAY_LABELS.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                week.forEach { cellData ->
                    if (cellData == null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                        )
                    } else {
                        ActivityChartCell(
                            cellData = cellData,
                            config = config,
                            highlightDate = today,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Раскладывает дни месяца в недели (списки по 7 элементов).
 * Индекс колонки = (dayOfWeek - 1), понедельник первый; null — пустая позиция.
 */
private fun buildCalendarWeeks(
    daysInMonth: List<Date>,
    sessions: List<DailyReadingStats>,
    maxPages: Int,
): List<List<ChartCellData?>> {
    if (daysInMonth.isEmpty()) return emptyList()

    val sessionByDate = sessions.associateBy { it.date }
    val weeks = mutableListOf<MutableList<ChartCellData?>>()
    var currentWeek = MutableList<ChartCellData?>(DAYS_IN_WEEK) { null }

    val leadingEmpty = daysInMonth.first().dayOfWeek.value - 1
    var column = leadingEmpty

    daysInMonth.forEach { day ->
        if (column >= DAYS_IN_WEEK) {
            weeks.add(currentWeek)
            currentWeek = MutableList(DAYS_IN_WEEK) { null }
            column = 0
        }

        val pagesRead = sessionByDate[day.dateKey]?.totalPagesRead ?: 0
        currentWeek[column] = ChartCellData(
            date = day,
            intensity = getRelativeActivityIntensity(pagesRead, maxPages),
            pagesRead = pagesRead,
        )
        column++
    }
    weeks.add(currentWeek)

    return weeks
}

@Preview(name = "MonthActivityChart Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun MonthActivityChartPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                MonthActivityChart(
                    year = 2026,
                    month = 6,
                    sessions = ProductivityPreviewData.generateMonthData(year = 2026, month = 6),
                )
            }
        }
    }
}

@Preview(name = "MonthActivityChart Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun MonthActivityChartPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                MonthActivityChart(
                    year = 2026,
                    month = 6,
                    sessions = ProductivityPreviewData.generateMonthData(year = 2026, month = 6),
                )
            }
        }
    }
}
