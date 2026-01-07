package fuwafuwa.time.bookechi.base.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.time.Date
import fuwafuwa.time.bookechi.base.time.getDaysInYear
import fuwafuwa.time.bookechi.base.time.getWeeksInYear
import fuwafuwa.time.bookechi.data.model.getRelativeActivityIntensity

@Composable
fun YearQuadActivityChart(
    year: Int,
    modifier: Modifier = Modifier,
    config: ActivityChartConfig = ActivityChartConfig(),
    readingData: Map<String, Int> = emptyMap(),
) {
    val weeksInYear = remember(year) { getWeeksInYear(year) }
    val daysInYear = remember(year) { getDaysInYear(year) }

    val cellsData = remember(year, readingData) {
        prepareCellsDataForYear(daysInYear, weeksInYear, readingData)
    }

    if (config.zoomMode) {
        ZoomedYearChart(
            weeksInYear = weeksInYear,
            cellsData = cellsData,
            config = config,
            modifier = modifier
        )
    } else {
        CompactYearChart(
            weeksInYear = weeksInYear,
            cellsData = cellsData,
            config = config,
            modifier = modifier
        )
    }
}

@Composable
private fun CompactYearChart(
    weeksInYear: Int,
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val cellSize = maxWidth / weeksInYear

        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until weeksInYear) {
                        val cellIndex = row * weeksInYear + col
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
private fun ZoomedYearChart(
    weeksInYear: Int,
    cellsData: List<ChartCellData>,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val cellSizePx = with(density) { config.zoomedItemSize.toPx() }
    val cellSpacingPx = with(density) { config.cellSpacing.toPx() }
    val strokeWidthPx = with(density) { config.separatorWidth.toPx() }
    val dashLengthPx = with(density) { config.separatorDashLength.toPx() }
    val dashGapPx = with(density) { config.separatorDashGap.toPx() }

    val monthGrid = remember(cellsData, weeksInYear) {
        Array(7) { row ->
            IntArray(weeksInYear) { col ->
                val cellIndex = row * weeksInYear + col
                cellsData.getOrNull(cellIndex)?.date?.month ?: 0
            }
        }
    }

    val boundaryPath = remember(monthGrid, cellSizePx, cellSpacingPx, config.showMonthSeparators) {
        if (!config.showMonthSeparators) Path()
        else buildBoundaryPathInGaps(monthGrid, weeksInYear, cellSizePx, cellSpacingPx)
    }

    val strokeCap = if (config.separatorRoundedCorners) StrokeCap.Round else StrokeCap.Butt
    val strokeJoin = if (config.separatorRoundedCorners) StrokeJoin.Round else StrokeJoin.Miter

    val pathEffect = when (config.separatorStyle) {
        SeparatorStyle.Solid -> null
        SeparatorStyle.Dashed -> PathEffect.dashPathEffect(floatArrayOf(dashLengthPx, dashGapPx))
        SeparatorStyle.Dotted -> PathEffect.dashPathEffect(floatArrayOf(strokeWidthPx, dashGapPx))
    }

    Box(modifier = modifier.horizontalScroll(scrollState)) {
        Row(horizontalArrangement = Arrangement.spacedBy(config.cellSpacing)) {
            for (col in 0 until weeksInYear) {
                Column(verticalArrangement = Arrangement.spacedBy(config.cellSpacing)) {
                    for (row in 0 until 7) {
                        val cellIndex = row * weeksInYear + col
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

        if (config.showMonthSeparators) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawPath(
                    path = boundaryPath,
                    color = config.monthSeparatorColor,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = strokeCap,
                        join = strokeJoin,
                        pathEffect = pathEffect
                    )
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
private fun buildBoundaryPathInGaps(
    monthGrid: Array<IntArray>,
    columnsCount: Int,
    cellSizePx: Float,
    cellSpacingPx: Float
): Path {
    val path = Path()
    val step = cellSizePx + cellSpacingPx
    val halfSpacing = cellSpacingPx / 2
    
    // Конвертирует точку сетки в пиксели
    fun toPixel(col: Int, row: Int): Pair<Float, Float> {
        return (col * step - halfSpacing) to (row * step - halfSpacing)
    }

    // Для каждой пары соседних месяцев
    for (month in 2..12) {
        val prevMonth = month - 1
        
        // Собираем все сегменты границы
        val segments = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        
        for (row in 0 until 7) {
            for (col in 0 until columnsCount) {
                val current = monthGrid[row][col]
                
                // Вертикальный сегмент: новый месяц справа от старого
                if (col > 0 && current == month && monthGrid[row][col - 1] == prevMonth) {
                    segments.add((col to row) to (col to row + 1))
                }
                
                // Горизонтальный сегмент: новый месяц под старым
                if (row > 0 && current == month && monthGrid[row - 1][col] == prevMonth) {
                    segments.add((col to row) to (col + 1 to row))
                }
            }
        }
        
        if (segments.isEmpty()) continue
        
        // Соединяем сегменты в цепочки
        val chains = buildChains(segments)
        
        // Рисуем каждую цепочку
        for (chain in chains) {
            if (chain.size < 2) continue
            val (x0, y0) = toPixel(chain[0].first, chain[0].second)
            path.moveTo(x0, y0)
            for (i in 1 until chain.size) {
                val (x, y) = toPixel(chain[i].first, chain[i].second)
                path.lineTo(x, y)
            }
        }
    }

    return path
}

/**
 * Соединяет сегменты в цепочки точек.
 * Если конец одного сегмента совпадает с началом другого — они соединяются.
 */
private fun buildChains(
    segments: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>
): List<List<Pair<Int, Int>>> {
    if (segments.isEmpty()) return emptyList()
    
    val remaining = segments.toMutableList()
    val chains = mutableListOf<MutableList<Pair<Int, Int>>>()
    
    while (remaining.isNotEmpty()) {
        // Начинаем новую цепочку
        val (start, end) = remaining.removeAt(0)
        val chain = mutableListOf(start, end)
        
        // Пытаемся продолжить цепочку
        var extended = true
        while (extended) {
            extended = false
            
            // Ищем сегмент, начинающийся с конца цепочки
            val last = chain.last()
            val nextIdx = remaining.indexOfFirst { it.first == last }
            if (nextIdx >= 0) {
                chain.add(remaining[nextIdx].second)
                remaining.removeAt(nextIdx)
                extended = true
                continue
            }
            
            // Ищем сегмент, заканчивающийся в начале цепочки
            val first = chain.first()
            val prevIdx = remaining.indexOfFirst { it.second == first }
            if (prevIdx >= 0) {
                chain.add(0, remaining[prevIdx].first)
                remaining.removeAt(prevIdx)
                extended = true
            }
        }
        
        chains.add(chain)
    }
    
    return chains
}

internal fun prepareCellsDataForYear(
    daysInYear: List<Date>,
    weeksInYear: Int,
    readingData: Map<String, Int>
): List<ChartCellData> {
    val maxPages = readingData.values.maxOrNull() ?: 0
    val cells = mutableListOf<ChartCellData>()

    for (row in 0 until 7) {
        for (col in 0 until weeksInYear) {
            val dayOfWeek = row + 1
            val weekOfYear = col + 1

            val day = daysInYear.find {
                it.dayOfWeek.value == dayOfWeek && it.weekOfYear == weekOfYear
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

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun YearChartActivityStylePreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearQuadActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                zoomMode = true,
                showPadding = true,
                showMonthSeparators = true,
                zoomedItemSize = 18.dp,
                colorScheme = ActivityColorScheme.Activity
            ),
            readingData = mapOf(
                "2026-01-05" to 5,
                "2026-01-10" to 15,
                "2026-01-15" to 30,
                "2026-01-20" to 45,
                "2026-01-25" to 60,
                "2026-02-03" to 20,
                "2026-02-10" to 35,
                "2026-02-20" to 55,
                "2026-03-05" to 25,
                "2026-03-10" to 70,
                "2026-03-15" to 40,
                "2026-04-01" to 15,
                "2026-04-15" to 50,
                "2026-05-05" to 80,
                "2026-05-20" to 30
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun YearChartNoSeparatorsPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearQuadActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                zoomMode = true,
                showPadding = true,
                showMonthSeparators = false,
                zoomedItemSize = 18.dp,
                colorScheme = ActivityColorScheme.Activity
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun YearChartNoPaddingPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearQuadActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                zoomMode = true,
                showPadding = false,
                showMonthSeparators = true,
                zoomedItemSize = 18.dp,
                separatorWidth = 1.dp,
                colorScheme = ActivityColorScheme.Activity
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun YearChartOrangePreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearQuadActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                zoomMode = true,
                showPadding = true,
                showMonthSeparators = true,
                zoomedItemSize = 18.dp,
                colorScheme = ActivityColorScheme.Orange
            ),
            readingData = mapOf(
                "2026-01-15" to 10,
                "2026-01-16" to 25,
                "2026-01-17" to 50,
                "2026-01-18" to 75,
                "2026-01-19" to 100,
                "2026-02-20" to 60,
                "2026-03-10" to 40
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun YearChartDashedSeparatorPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearQuadActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                zoomMode = true,
                showMonthSeparators = true,
                zoomedItemSize = 18.dp,
                separatorStyle = SeparatorStyle.Dashed,
                separatorWidth = 2.dp,
                separatorDashLength = 6.dp,
                separatorDashGap = 3.dp,
                monthSeparatorColor = Color(0xFF6366F1),
                colorScheme = ActivityColorScheme.Purple
            ),
            readingData = mapOf(
                "2026-01-15" to 30,
                "2026-02-10" to 60,
                "2026-03-20" to 45
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun YearChartDottedSeparatorPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearQuadActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                zoomMode = true,
                showMonthSeparators = true,
                zoomedItemSize = 18.dp,
                separatorStyle = SeparatorStyle.Dotted,
                separatorWidth = 2.dp,
                separatorDashGap = 4.dp,
                monthSeparatorColor = Color(0xFFEC4899),
                colorScheme = ActivityColorScheme.Activity
            ),
            readingData = mapOf(
                "2026-01-15" to 30,
                "2026-02-10" to 60,
                "2026-03-20" to 45
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 1200)
@Composable
private fun YearChartStyledSeparatorPreview() {
    Column(modifier = Modifier.padding(8.dp)) {
        YearQuadActivityChart(
            year = 2026,
            config = ActivityChartConfig(
                zoomMode = true,
                showMonthSeparators = true,
                zoomedItemSize = 18.dp,
                cellSpacing = 4.dp,
                separatorStyle = SeparatorStyle.Solid,
                separatorWidth = 2.5.dp,
                separatorRoundedCorners = true,
                monthSeparatorColor = Color(0xFF10B981).copy(alpha = 0.8f),
                colorScheme = ActivityColorScheme.Activity
            ),
            readingData = mapOf(
                "2026-01-15" to 30,
                "2026-02-10" to 60,
                "2026-03-20" to 45,
                "2026-04-05" to 80
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
