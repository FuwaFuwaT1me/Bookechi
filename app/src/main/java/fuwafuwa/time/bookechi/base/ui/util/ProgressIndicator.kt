package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun DrawScope.drawLinearIndicatorBackground(
    color: Color,
    cornerRadius: Dp,
    topLeftOffset: Offset = Offset.Zero,
    size: Size = drawContext.size,
) {
    drawLinearIndicator(1f, color, cornerRadius, topLeftOffset, size)
}

internal fun DrawScope.drawLinearIndicator(
    widthFraction: Float,
    color: Color,
    cornerRadius: Dp,
    topLeftOffset: Offset = Offset.Zero,
    size: Size = drawContext.size,
) {
    drawRoundRect(
        topLeft = topLeftOffset,
        color = color,
        size = size.copy(width = size.width * widthFraction),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
    )
}

@Composable
fun SimpleProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float = 0.7f,
    progressBarColor: Color = Color.Red,
    cornerRadius: Dp = 0.dp,
    trackColor: Color = Color(0XFFFBE8E8),
    innerProgressBarPadding: Dp = 0.dp,
) {
    Canvas(
        modifier
            .progressSemantics(progress)
    ) {
        drawLinearIndicatorBackground(
            trackColor,
            cornerRadius,
        )

        val axisOffset = innerProgressBarPadding.toPx() / 2f

        drawLinearIndicator(
            progress,
            progressBarColor,
            cornerRadius,
            topLeftOffset = Offset(axisOffset, axisOffset),
            size = size.copy(height = size.height - innerProgressBarPadding.toPx())
        )
    }
}

@Composable
@Preview
private fun SimpleProgressIndicatorPreview() {
    SimpleProgressIndicator(
        modifier = Modifier
            .width(100.dp)
            .height(10.dp),
        progress = 0.7f,
        progressBarColor = Color.Red,
        cornerRadius = 8.dp,
        trackColor = Color(0XFFFBE8E8),
        innerProgressBarPadding = 2.dp
    )
}
