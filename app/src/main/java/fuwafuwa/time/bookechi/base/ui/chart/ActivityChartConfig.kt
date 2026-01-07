package fuwafuwa.time.bookechi.base.ui.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ActivityChartConfig(
    val showPadding: Boolean = true,
    val itemPadding: Dp = 2.dp,
    val cellSpacing: Dp = 3.dp,
    val showMonthSeparators: Boolean = true,
    val monthSeparatorColor: Color = Color.Gray.copy(alpha = 0.6f),
    val separatorWidth: Dp = 1.5.dp,
    val separatorStyle: SeparatorStyle = SeparatorStyle.Solid,
    val separatorDashLength: Dp = 4.dp,
    val separatorDashGap: Dp = 2.dp,
    val separatorRoundedCorners: Boolean = true,
    val zoomMode: Boolean = false,
    val zoomedItemSize: Dp = 18.dp,
    val cornerRadius: Dp = 2.dp,
    val colorScheme: ActivityColorScheme = ActivityColorScheme.Activity
)

enum class SeparatorStyle {
    Solid,
    Dashed,
    Dotted
}
