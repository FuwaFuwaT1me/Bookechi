package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ActivityColorScheme
import fuwafuwa.time.bookechi.base.ui.chart.ActivityIntensity
import fuwafuwa.time.bookechi.base.ui.chart.ChartCellData
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellZeroActivityStroke

@Composable
fun ActivityChartCell(
    cellData: ChartCellData?,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    val color = getCellColor(cellData, config)
    val shape = RoundedCornerShape(config.cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .then(
                if (cellData?.intensity == ActivityIntensity.NONE) {
                    Modifier.border(
                        width = 1.dp,
                        color = FigmaActivityCellZeroActivityStroke,
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
                    .background(config.colorScheme.getColor(intensity))
                    .then(
                        if (intensity == ActivityIntensity.NONE) {
                            Modifier.border(
                                width = 1.dp,
                                color = FigmaActivityCellZeroActivityStroke,
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

internal fun getCellColor(cellData: ChartCellData?, config: ActivityChartConfig): Color {
    if (cellData?.date == null) {
        return Color.Transparent
    }
    return config.colorScheme.getColor(cellData.intensity)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ColorSchemesPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            ActivityColorScheme.OrangeActivity to "OrangeActivity",
        ).forEach { (scheme, _) ->
            ActivityChartLegend(
                config = ActivityChartConfig(colorScheme = scheme)
            )
        }
    }
}
