package fuwafuwa.time.bookechi.base.ui.chart

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ActivityChartConfig(
    val showSpacing: Boolean = true,
    val itemHorizontalSpacing: Dp = 2.dp,
    val itemVerticalSpacing: Dp = 2.dp,
    val cornerRadius: Dp = 2.dp,
    val colorScheme: ActivityColorScheme = ActivityColorScheme.OrangeActivity
)
