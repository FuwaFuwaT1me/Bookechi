package fuwafuwa.time.bookechi.base.ui.chart

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.time.Date
import fuwafuwa.time.bookechi.base.time.getDaysInMonth
import fuwafuwa.time.bookechi.base.time.getWeeksInMonth
import fuwafuwa.time.bookechi.data.model.getRelativeActivityIntensity

@Composable
fun MonthQuadActivityChart(
    year: Int,
    month: Int,
    modifier: Modifier = Modifier,
    config: ActivityChartConfig = ActivityChartConfig(),
    readingData: Map<String, Int> = emptyMap(),
) {
    val weeksInMonth = remember(year, month) { getWeeksInMonth(year, month) }
    val daysInMonth = remember(year, month) { getDaysInMonth(year, month) }

    val cellsData = remember(year, month, readingData) {
        prepareCellsDataForMonth(daysInMonth, weeksInMonth, readingData)
    }

    if (config.zoomMode) {
        ZoomedMonthChart(
            weeksInMonth = weeksInMonth,
            cellsData = cellsData,
            config = config,
            modifier = modifier
        )
    } else {
        CompactMonthChart(
            weeksInMonth = weeksInMonth,
            cellsData = cellsData,
            config = config,
            modifier = modifier
        )
    }
}

@Composable
private fun CompactMonthChart(
    weeksInMonth: Int,
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val cellSize = maxWidth / weeksInMonth

        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until 7) {
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

@Composable
private fun ZoomedMonthChart(
    weeksInMonth: Int,
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier.horizontalScroll(scrollState)
    ) {
        for (col in 0 until weeksInMonth) {
            Column {
                for (row in 0 until 7) {
                    val cellIndex = row * weeksInMonth + col
                    val cellData = cellsData.getOrNull(cellIndex)

                    ActivityChartCell(
                        cellData = cellData,
                        config = config,
                        modifier = Modifier.size(config.zoomedItemSize)
                    )
                }
            }
        }
    }
}

internal fun prepareCellsDataForMonth(
    daysInMonth: List<Date>,
    weeksInMonth: Int,
    readingData: Map<String, Int>
): List<ChartCellData> {
    val maxPages = readingData.values.maxOrNull() ?: 0
    val cells = mutableListOf<ChartCellData>()

    for (row in 0 until 7) {
        for (col in 0 until weeksInMonth) {
            val dayOfWeek = row + 1
            val weekOfMonth = col + 1

            val day = daysInMonth.find {
                it.dayOfWeek.value == dayOfWeek && it.weekOfMonth == weekOfMonth
            }

            val pagesRead = day?.let { readingData[it.dateKey] } ?: 0

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

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MonthChartPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        MonthQuadActivityChart(
            year = 2026,
            month = 1,
            config = ActivityChartConfig(
                zoomMode = true,
                showPadding = true,
                zoomedItemSize = 18.dp,
                colorScheme = ActivityColorScheme.Activity
            ),
            readingData = mapOf(
                "2026-01-05" to 5,
                "2026-01-06" to 12,
                "2026-01-07" to 8,
                "2026-01-10" to 25,
                "2026-01-12" to 30,
                "2026-01-15" to 50,
                "2026-01-18" to 20,
                "2026-01-20" to 80,
                "2026-01-22" to 45,
                "2026-01-25" to 60
            )
        )
    }
}
