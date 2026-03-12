package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.time.Date
import fuwafuwa.time.bookechi.base.time.getDaysInYear
import fuwafuwa.time.bookechi.base.time.getWeeksInYear
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ActivityColorScheme
import fuwafuwa.time.bookechi.base.ui.chart.ChartCellData
import fuwafuwa.time.bookechi.base.ui.chart.YearActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.getRelativeActivityIntensity
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData
import java.time.LocalDate

private const val DAYS_IN_WEEK = 7
private const val COMPACT_HORIZONTAL_YEAR_COLUMNS = 25

@Composable
fun YearActivityChart(
    year: Int,
    modifier: Modifier = Modifier,
    isHorizontal: Boolean = true,
    config: ActivityChartConfig = ActivityChartConfig(),
    yearConfig: YearActivityChartConfig = YearActivityChartConfig(),
    sessions: List<DailyReadingStats> = emptyList(),
) {
    val weeksInYear = remember(year) { getWeeksInYear(year) }
    val daysInYear = remember(year) { getDaysInYear(year) }
    val today = LocalDate.now()

    val (weekGridCells, linearCells) = remember(year, sessions) {
        val maxPages = sessions.maxOfOrNull { it.totalPagesRead } ?: 0
        val weekGrid = buildWeekGridCells(
            daysInYear = daysInYear,
            weeksInYear = weeksInYear,
            sessions = sessions,
            maxPages = maxPages
        )
        val linear = buildLinearYearCells(
            daysInYear = daysInYear,
            sessions = sessions,
            maxPages = maxPages
        )
        weekGrid to linear
    }

    if (yearConfig.zoomMode) {
        ZoomedYearChart(
            weeksInYear = weeksInYear,
            cellsData = weekGridCells,
            config = config,
            yearConfig = yearConfig,
            highlightDate = today,
            modifier = modifier,
        )
    } else {
        if (isHorizontal) {
            CompactHorizontalYearChart(
                cellsData = linearCells,
                config = config,
                highlightDate = today,
                modifier = modifier
            )
        } else {
            CompactVerticalYearChart(
                weeksInYear = weeksInYear,
                cellsData = weekGridCells,
                config = config,
                highlightDate = today,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CompactVerticalYearChart(
    weeksInYear: Int,
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    highlightDate: LocalDate,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val cellSize = maxWidth / weeksInYear

        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until DAYS_IN_WEEK) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until weeksInYear) {
                        val cellIndex = row * weeksInYear + col
                        val cellData = cellsData.getOrNull(cellIndex)

                        ActivityChartCell(
                            cellData = cellData,
                            config = config,
                            highlightDate = highlightDate,
                            modifier = Modifier.size(cellSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactHorizontalYearChart(
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    highlightDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val verticalSpacing = if (config.showSpacing) config.itemVerticalSpacing else 0.dp
    val horizontalSpacing = if (config.showSpacing) config.itemHorizontalSpacing else 0.dp
    val rows =
        (cellsData.size + COMPACT_HORIZONTAL_YEAR_COLUMNS - 1) / COMPACT_HORIZONTAL_YEAR_COLUMNS

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                for (col in 0 until COMPACT_HORIZONTAL_YEAR_COLUMNS) {
                    val cellIndex = row * COMPACT_HORIZONTAL_YEAR_COLUMNS + col
                    val cellData = cellsData.getOrNull(cellIndex)

                    ActivityChartCell(
                        cellData = cellData,
                        config = config,
                        highlightDate = highlightDate,
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
private fun ZoomedYearChart(
    weeksInYear: Int,
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    yearConfig: YearActivityChartConfig,
    highlightDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val cellSizePx = with(density) { yearConfig.zoomedItemSize.toPx() }
    val cellSpacingPx = with(density) { yearConfig.cellSpacing.toPx() }

    val monthGrid = remember(cellsData, weeksInYear) {
        Array(DAYS_IN_WEEK) { row ->
            IntArray(weeksInYear) { col ->
                val cellIndex = row * weeksInYear + col
                cellsData.getOrNull(cellIndex)?.date?.month ?: 0
            }
        }
    }

    val boundaryPath = remember(monthGrid, cellSizePx, cellSpacingPx, yearConfig.showMonthSeparators) {
        buildBoundaryPathInGaps(monthGrid, weeksInYear, cellSizePx, cellSpacingPx)
    }

    Box(modifier = modifier.horizontalScroll(scrollState)) {
        Row(horizontalArrangement = Arrangement.spacedBy(yearConfig.cellSpacing)) {
            for (col in 0 until weeksInYear) {
                Column(verticalArrangement = Arrangement.spacedBy(yearConfig.cellSpacing)) {
                    for (row in 0 until DAYS_IN_WEEK) {
                        val cellIndex = row * weeksInYear + col
                        val cellData = cellsData.getOrNull(cellIndex)

                        ActivityChartCell(
                            cellData = cellData,
                            config = config,
                            highlightDate = highlightDate,
                            modifier = Modifier.size(yearConfig.zoomedItemSize)
                        )
                    }
                }
            }
        }

        if (yearConfig.showMonthSeparators) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawPath(
                    path = boundaryPath,
                    color = Color.Green
                )
            }
        }
    }
}

/**
 * Строит ломаную линию границы между месяцами.
 *
 * Алгоритм:
 * 1. Для каждой пары месяцев (янв-фев, фев-мар, ...) собираем сегменты границы
 * 2. Сегмент = линия между двумя точками в промежутках между ячейками
 * 3. Сортируем сегменты так, чтобы конец одного совпадал с началом следующего
 * 4. Рисуем непрерывную линию
 */
private data class GridPoint(
    val col: Int,
    val row: Int,
)

private data class GridSegment(
    val start: GridPoint,
    val end: GridPoint
)

private fun buildBoundaryPathInGaps(
    monthGrid: Array<IntArray>,
    columnsCount: Int,
    cellSizePx: Float,
    cellSpacingPx: Float
): Path {
    val path = Path()
    val step = cellSizePx + cellSpacingPx
    val halfSpacing = cellSpacingPx / 2

    fun toPixel(point: GridPoint): Pair<Float, Float> {
        return (point.col * step - halfSpacing) to (point.row * step - halfSpacing)
    }

    // Для каждой пары соседних месяцев
    for (month in 2..12) {
        val segments = collectMonthBoundarySegments(
            monthGrid = monthGrid,
            columnsCount = columnsCount,
            month = month,
        )

        if (segments.isEmpty()) continue

        // Соединяем сегменты в цепочки
        val chains = buildChains(segments)

        // Рисуем каждую цепочку
        for (chain in chains) {
            if (chain.size < 2) continue
            val (x0, y0) = toPixel(chain[0])
            path.moveTo(x0, y0)
            for (i in 1 until chain.size) {
                val (x, y) = toPixel(chain[i])
                path.lineTo(x, y)
            }
        }
    }

    return path
}

private fun collectMonthBoundarySegments(
    monthGrid: Array<IntArray>,
    columnsCount: Int,
    month: Int
): List<GridSegment> {
    val prevMonth = month - 1
    val segments = mutableListOf<GridSegment>()

    for (row in 0 until DAYS_IN_WEEK) {
        for (col in 0 until columnsCount) {
            val current = monthGrid[row][col]

            if (isVerticalBoundary(monthGrid, col, row, current, prevMonth)) {
                segments.add(
                    GridSegment(
                        start = GridPoint(col, row),
                        end = GridPoint(col, row + 1)
                    )
                )
            }

            if (isHorizontalBoundary(monthGrid, col, row, current, prevMonth)) {
                segments.add(
                    GridSegment(
                        start = GridPoint(col, row),
                        end = GridPoint(col + 1, row)
                    )
                )
            }
        }
    }

    return segments
}

private fun isVerticalBoundary(
    monthGrid: Array<IntArray>,
    col: Int,
    row: Int,
    currentMonth: Int,
    prevMonth: Int
): Boolean {
    return col > 0 &&
            currentMonth == prevMonth + 1 &&
            monthGrid[row][col - 1] == prevMonth
}

private fun isHorizontalBoundary(
    monthGrid: Array<IntArray>,
    col: Int,
    row: Int,
    currentMonth: Int,
    prevMonth: Int,
): Boolean {
    return row > 0 &&
            currentMonth == prevMonth + 1 &&
            monthGrid[row - 1][col] == prevMonth
}

/**
 * Соединяет сегменты в цепочки точек.
 * Если конец одного сегмента совпадает с началом другого — они соединяются.
 */
private fun buildChains(
    segments: List<GridSegment>,
): List<List<GridPoint>> {
    if (segments.isEmpty()) return emptyList()

    val remaining = segments.toMutableList()
    val chains = mutableListOf<MutableList<GridPoint>>()

    while (remaining.isNotEmpty()) {
        // Начинаем новую цепочку
        val segment = remaining.removeAt(0)
        val chain = mutableListOf(segment.start, segment.end)

        // Пытаемся продолжить цепочку
        var extended = true
        while (extended) {
            extended = false

            // Ищем сегмент, начинающийся с конца цепочки
            val last = chain.last()
            val nextIdx = remaining.indexOfFirst { it.start == last }
            if (nextIdx >= 0) {
                chain.add(remaining[nextIdx].end)
                remaining.removeAt(nextIdx)
                extended = true
                continue
            }

            // Ищем сегмент, заканчивающийся в начале цепочки
            val first = chain.first()
            val prevIdx = remaining.indexOfFirst { it.end == first }
            if (prevIdx >= 0) {
                chain.add(0, remaining[prevIdx].start)
                remaining.removeAt(prevIdx)
                extended = true
            }
        }

        chains.add(chain)
    }

    return chains
}

private fun buildWeekGridCells(
    daysInYear: List<Date>,
    weeksInYear: Int,
    sessions: List<DailyReadingStats>,
    maxPages: Int
): List<ChartCellData> {
    val dayByGridKey = daysInYear.associateBy { gridKey(it.weekOfYear, it.dayOfWeek.value) }
    val cells = ArrayList<ChartCellData>(weeksInYear * DAYS_IN_WEEK)

    for (row in 0 until DAYS_IN_WEEK) {
        for (col in 0 until weeksInYear) {
            val dayOfWeek = row + 1
            val weekOfYear = col + 1

            val day = dayByGridKey[gridKey(weekOfYear, dayOfWeek)]

            val pagesRead = day?.let {
                sessions.find { it.date == day.dateKey }?.totalPagesRead
            } ?: 0

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

private fun buildLinearYearCells(
    daysInYear: List<Date>,
    sessions: List<DailyReadingStats>,
    maxPages: Int
): List<ChartCellData> {
    return daysInYear.map { day ->
        val pagesRead = sessions.find { it.date == day.dateKey }?.totalPagesRead ?: 0
        ChartCellData(
            date = day,
            intensity = getRelativeActivityIntensity(pagesRead, maxPages),
            pagesRead = pagesRead
        )
    }
}

private fun gridKey(weekOfYear: Int, dayOfWeek: Int): Int {
    return weekOfYear * 100 + dayOfWeek
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun PreviewVerticalCompact() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                colorScheme = ActivityColorScheme.OrangeActivity
            ),
            yearConfig = YearActivityChartConfig(),
            sessions = ProductivityPreviewData.generateYearData(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun PreviewHorizontalCompact() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearActivityChart(
            year = 2026,
            isHorizontal = true,
            config = ActivityChartConfig(
                colorScheme = ActivityColorScheme.OrangeActivity
            ),
            yearConfig = YearActivityChartConfig(),
            sessions = ProductivityPreviewData.generateYearData(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
