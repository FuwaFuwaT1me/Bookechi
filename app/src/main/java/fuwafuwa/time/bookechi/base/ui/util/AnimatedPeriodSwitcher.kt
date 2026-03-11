package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

data class AnimatedPeriodSwitcherConfig(
    val containerColor: Color = Color(0xFFE8DFDB),
    val selectedColor: Color = Color.White,
    val activeTextColor: Color = Color(0xFF2F1B0F),
    val inactiveTextColor: Color = Color(0xFF9A8E88),
)

@Composable
fun AnimatedPeriodSwitcher(
    values: List<String>,
    modifier: Modifier = Modifier
        .height(72.dp)
    ,
    selectedIndex: Int = 0,
    innerCornerRadius: Dp = 20.dp,
    outerCornerRadius: Dp = 20.dp,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    itemSpacing: Dp = 8.dp,
    onSwitch: (Int) -> Unit,
    config: AnimatedPeriodSwitcherConfig = AnimatedPeriodSwitcherConfig(),
) {
    var rowSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    val itemWidth = with(density) {
        ((rowSize.width.toDp() - itemSpacing * (values.size - 1)) / values.size)
    }

    val indicatorOffset by animateDpAsState(
        targetValue = when (selectedIndex) {
            0 -> 0.dp
            else -> (itemWidth + itemSpacing) * selectedIndex
        },
        label = "indicatorOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(config.containerColor, RoundedCornerShape(outerCornerRadius))
            .padding(horizontal = horizontalSpacing, vertical = verticalSpacing)
            .onSizeChanged { rowSize = it }
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(itemWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(innerCornerRadius))
                .background(config.selectedColor)
        )

        Row(
            modifier = Modifier.fillMaxSize()
            ,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            values.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(innerCornerRadius))
                        .clickable { onSwitch(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (selectedIndex == index) config.activeTextColor else config.inactiveTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAnimatedPeriodSwitcher() {
    AnimatedPeriodSwitcher(
        values = listOf("День", "Неделя", "Месяц", "Год"),
        onSwitch = {}
    )
}

@Preview
@Composable
private fun PreviewAnimatedPeriodSwitcher_2() {
    AnimatedPeriodSwitcher(
        values = listOf("Месяц", "Год"),
        onSwitch = {}
    )
}
