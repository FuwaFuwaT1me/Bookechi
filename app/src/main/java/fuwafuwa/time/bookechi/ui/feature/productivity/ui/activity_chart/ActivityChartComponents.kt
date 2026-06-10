package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ActivityIntensity
import fuwafuwa.time.bookechi.base.ui.chart.ChartCellData
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import java.time.LocalDate

/**
 * Цвет ячейки тепловой карты берётся из дизайн-системы:
 * BookechiTheme.colors.heatmap — список из 6 цветов (индекс 0..5),
 * где индекс соответствует ActivityIntensity.ordinal (NONE..VERY_HIGH).
 * Граница нулевой ячейки — heatZeroStroke. Подсветка «сегодня» — accent.
 */
@Composable
fun ActivityChartCell(
    cellData: ChartCellData?,
    config: ActivityChartConfig,
    highlightDate: LocalDate? = null,
    modifier: Modifier = Modifier
) {
    val colors = BookechiTheme.colors
    val color = cellColor(cellData)
    val shape = RoundedCornerShape(config.cornerRadius)
    val isHighlighted = highlightDate != null && cellData?.date?.localDate == highlightDate

    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .then(
                if (isHighlighted) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = colors.accent,
                        shape = shape
                    )
                } else if (cellData?.intensity == ActivityIntensity.NONE) {
                    Modifier.border(
                        width = 1.dp,
                        color = colors.heatZeroStroke,
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
    )
}

@Composable
fun ActivityChartLegend(
    modifier: Modifier = Modifier,
    config: ActivityChartConfig = ActivityChartConfig()
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActivityIntensity.entries.forEach { intensity ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(
                        if (intensity == ActivityIntensity.NONE) {
                            RoundedCornerShape(4.dp)
                        } else {
                            RoundedCornerShape(config.cornerRadius)
                        }
                    )
                    .background(colors.heatmap[intensity.ordinal])
                    .then(
                        if (intensity == ActivityIntensity.NONE) {
                            Modifier.border(
                                width = 1.dp,
                                color = colors.heatZeroStroke,
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

/** Цвет ячейки по интенсивности из палитры heatmap дизайн-системы. */
@Composable
private fun cellColor(cellData: ChartCellData?): Color {
    if (cellData?.date == null) {
        return Color.Transparent
    }
    return BookechiTheme.colors.heatmap[cellData.intensity.ordinal]
}

@Preview(name = "Legend Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun ActivityChartLegendPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(16.dp)) {
                ActivityChartLegend(config = ActivityChartConfig(cornerRadius = 2.dp))
            }
        }
    }
}

@Preview(name = "Legend Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun ActivityChartLegendPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(16.dp)) {
                ActivityChartLegend(config = ActivityChartConfig(cornerRadius = 2.dp))
            }
        }
    }
}
