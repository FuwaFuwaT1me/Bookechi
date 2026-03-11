package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.time.Date
import fuwafuwa.time.bookechi.base.time.getDaysInMonth
import fuwafuwa.time.bookechi.base.time.getWeeksInMonth
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ActivityColorScheme
import fuwafuwa.time.bookechi.base.ui.chart.ChartCellData
import fuwafuwa.time.bookechi.base.ui.chart.getRelativeActivityIntensity
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData

private const val DAYS_IN_WEEK = 7
private const val COMPACT_HORIZONTAL_MONTH_COLUMNS = 10

@Composable
fun MonthActivityChart(
    year: Int,
    month: Int,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
    config: ActivityChartConfig = ActivityChartConfig(),
    sessions: List<DailyReadingStats> = emptyList(),
) {
    val weeksInMonth = remember(year, month) { getWeeksInMonth(year, month) }
    val daysInMonth = remember(year, month) { getDaysInMonth(year, month) }

    val (weekGridCells, linearCells) = remember(year, month, sessions) {
        val maxPages = sessions.maxByOrNull { it.totalPagesRead }?.totalPagesRead ?: 0
        val weekGrid = buildWeekGridCellsForMonth(
            daysInMonth = daysInMonth,
            weeksInMonth = weeksInMonth,
            sessions = sessions,
            maxPages = maxPages
        )
        val linear = buildLinearMonthCells(
            daysInMonth = daysInMonth,
            sessions = sessions,
            maxPages = maxPages
        )
        weekGrid to linear
    }

    if (isHorizontal) {
        HorizontalMonthChart(
            cellsData = linearCells,
            config = config,
            modifier = modifier
        )
    } else {
        VerticalMonthChart(
            weeksInMonth = weeksInMonth,
            cellsData = weekGridCells,
            config = config,
            modifier = modifier
        )
    }
}

@Composable
private fun HorizontalMonthChart(
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    val verticalSpacing = if (config.showSpacing) config.itemVerticalSpacing else 0.dp
    val horizontalSpacing = if (config.showSpacing) config.itemHorizontalSpacing else 0.dp
    val rows =
        (cellsData.size + COMPACT_HORIZONTAL_MONTH_COLUMNS - 1) / COMPACT_HORIZONTAL_MONTH_COLUMNS

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                for (col in 0 until COMPACT_HORIZONTAL_MONTH_COLUMNS) {
                    val cellIndex = row * COMPACT_HORIZONTAL_MONTH_COLUMNS + col
                    val cellData = cellsData.getOrNull(cellIndex)

                    ActivityChartCell(
                        cellData = cellData,
                        config = config,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalMonthChart(
    weeksInMonth: Int,
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val cellSize = maxWidth / weeksInMonth

        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until DAYS_IN_WEEK) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until weeksInMonth) {
                        val cellIndex = row * weeksInMonth + col
                        val cellData = cellsData.getOrNull(cellIndex)

                        ActivityChartCell(
                            cellData = cellData,
                            config = config,
                            modifier = Modifier.size(cellSize)
                        )
                    }
                }
            }
        }
    }
}

private fun buildWeekGridCellsForMonth(
    daysInMonth: List<Date>,
    weeksInMonth: Int,
    sessions: List<DailyReadingStats>,
    maxPages: Int
): List<ChartCellData> {
    val sessionByDate = sessions.associateBy { it.date }
    val dayByGridKey = daysInMonth.associateBy { gridKey(it.weekOfMonth, it.dayOfWeek.value) }
    val cells = ArrayList<ChartCellData>(weeksInMonth * DAYS_IN_WEEK)

    for (row in 0 until DAYS_IN_WEEK) {
        for (col in 0 until weeksInMonth) {
            val dayOfWeek = row + 1
            val weekOfMonth = col + 1

            val day = dayByGridKey[gridKey(weekOfMonth, dayOfWeek)]
            val pagesRead = day?.let { sessionByDate[it.dateKey]?.totalPagesRead } ?: 0

            cells.add(
                ChartCellData(
                    date = day,
                    intensity = getRelativeActivityIntensity(pagesRead, maxPages),
                    pagesRead = pagesRead
                )
            )
        }
    }

    return cells
}

private fun buildLinearMonthCells(
    daysInMonth: List<Date>,
    sessions: List<DailyReadingStats>,
    maxPages: Int
): List<ChartCellData> {
    val sessionByDate = sessions.associateBy { it.date }
    return daysInMonth.map { day ->
        val pagesRead = sessionByDate[day.dateKey]?.totalPagesRead ?: 0
        ChartCellData(
            date = day,
            intensity = getRelativeActivityIntensity(pagesRead, maxPages),
            pagesRead = pagesRead
        )
    }
}

private fun gridKey(weekOfMonth: Int, dayOfWeek: Int): Int {
    return weekOfMonth * 100 + dayOfWeek
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun VerticalMonthChartPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        MonthActivityChart(
            year = 2026,
            month = 1,
            isHorizontal = false,
            config = ActivityChartConfig(
                showSpacing = true,
                colorScheme = ActivityColorScheme.OrangeActivity,
                cornerRadius = 4.dp
            ),
            sessions = ProductivityPreviewData.generateMonthData()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun HorizontalMonthChartPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        MonthActivityChart(
            year = 2026,
            month = 1,
            isHorizontal = true,
            config = ActivityChartConfig(
                showSpacing = true,
                colorScheme = ActivityColorScheme.OrangeActivity,
                cornerRadius = 4.dp
            ),
            sessions = ProductivityPreviewData.generateMonthData()
        )
    }
}
