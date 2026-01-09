package fuwafuwa.time.bookechi.base.ui.chart

import androidx.compose.foundation.background
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
import fuwafuwa.time.bookechi.data.model.ActivityIntensity

@Composable
fun ActivityChartCell(
    cellData: ChartCellData?,
    config: ActivityChartConfig,
    modifier: Modifier = Modifier
) {
    val color = getCellColor(cellData, config)
    val padding = if (config.showPadding) config.itemPadding else 0.dp

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clip(RoundedCornerShape(config.cornerRadius))
                .background(color)
        )
    }
}

@Composable
fun ActivityChartLegend(
    config: ActivityChartConfig = ActivityChartConfig(),
    modifier: Modifier = Modifier
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
                    .clip(RoundedCornerShape(2.dp))
                    .background(config.colorScheme.getColor(intensity))
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
            ActivityColorScheme.Activity to "Activity",
            ActivityColorScheme.Orange to "Orange",
            ActivityColorScheme.Blue to "Blue",
            ActivityColorScheme.Purple to "Purple"
        ).forEach { (scheme, _) ->
            ActivityChartLegend(
                config = ActivityChartConfig(colorScheme = scheme)
            )
        }
    }
}

