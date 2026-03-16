package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

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
    val clampedFraction = widthFraction.coerceIn(0f, 1f)
    val width = size.width * clampedFraction
    if (width <= 0f || size.height <= 0f) return
    val radius = cornerRadius.toPx().coerceAtMost(minOf(width, size.height) / 2f)
    drawRoundRect(
        topLeft = topLeftOffset,
        color = color,
        size = size.copy(width = width),
        cornerRadius = CornerRadius(radius, radius)
    )
}

data class DiffConfig(
    val lastProgress: Float,
    val diffColor: Color,
    val labelText: String? = null,
    val showPercent: Boolean = true,
    val showingPercentColor: Color = Color.Transparent,
)

@Composable
fun SimpleProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float = 0.7f,
    progressBarColor: Color = Color.Red,
    cornerRadius: Dp = 0.dp,
    trackColor: Color = Color(0XFFFBE8E8),
    innerProgressBarPadding: Dp = 0.dp,
    diffConfig: DiffConfig? = null,
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier
            .progressSemantics(progress)
    ) {
        drawLinearIndicatorBackground(
            trackColor,
            cornerRadius,
        )

        val innerPaddingPx = innerProgressBarPadding.toPx()
        val axisOffset = innerPaddingPx / 2f
        val barHeight = (size.height - innerPaddingPx).coerceAtLeast(0f)
        val barWidth = (size.width - innerPaddingPx).coerceAtLeast(0f)
        val barSize = size.copy(width = barWidth, height = barHeight)

        val clipPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, size),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )
            )
        }

        clipPath(clipPath) {
            if (diffConfig != null) {
                drawLinearIndicator(
                    progress,
                    diffConfig.diffColor,
                    cornerRadius,
                    topLeftOffset = Offset(axisOffset, axisOffset),
                    size = barSize
                )

                drawLinearIndicator(
                    diffConfig.lastProgress,
                    progressBarColor,
                    cornerRadius,
                    topLeftOffset = Offset(axisOffset, axisOffset),
                    size = barSize
                )
            } else {
                drawLinearIndicator(
                    progress,
                    progressBarColor,
                    cornerRadius,
                    topLeftOffset = Offset(axisOffset, axisOffset),
                    size = barSize
                )
            }
        }

        if (diffConfig != null && diffConfig.showPercent && barHeight > 0f) {
            val currentProgress = progress.coerceIn(0f, 1f)
            val lastProgress = diffConfig.lastProgress.coerceIn(0f, 1f)
            val diffText = diffConfig.labelText ?: run {
                val diffPercent = (currentProgress - lastProgress) * 100f
                if (diffPercent >= 0f) {
                    "+${diffPercent.roundToInt()}%"
                } else {
                    "${diffPercent.roundToInt()}%"
                }
            }

            val fontSize = (barHeight * 0.6f).toSp()
            val baseStyle = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold
            )
            val textLayout = textMeasurer.measure(text = diffText, style = baseStyle)
            val textWidth = textLayout.size.width.toFloat()
            val textHeight = textLayout.size.height.toFloat()
            val padding = 4.dp.toPx()

            val greenWidth = barSize.width * (currentProgress - lastProgress).coerceAtLeast(0f)
            val redWidth = barSize.width * lastProgress
            val fitsGreen = greenWidth >= textWidth + padding * 2 && greenWidth > 0f
            val fitsRed = redWidth >= textWidth + padding * 2 && redWidth > 0f
            val textTop = (axisOffset + (barHeight - textHeight) / 2f).coerceAtLeast(0f)

            val (textLeft, textColor) = when {
                fitsGreen -> {
                    val start = axisOffset + barSize.width * lastProgress
                    val left = start + greenWidth - textWidth - padding
                    left to Color.White
                }
                fitsRed -> {
                    val start = axisOffset
                    val left = start + redWidth - textWidth - padding
                    left to Color.White
                }
                else -> {
                    val start = axisOffset + barSize.width * currentProgress + padding
                    val rightEdge = axisOffset + barSize.width
                    val left = if (start + textWidth <= rightEdge) {
                        start
                    } else {
                        rightEdge - textWidth - padding
                    }
                    left to Color.Black
                }
            }

            val maxLeft = (axisOffset + barSize.width - textWidth).coerceAtLeast(axisOffset)
            val clampedLeft = textLeft.coerceIn(axisOffset, maxLeft)

            drawText(
                textMeasurer = textMeasurer,
                text = diffText,
                topLeft = Offset(clampedLeft, textTop),
                style = baseStyle.copy(color = textColor)
            )
        }
    }
}

@Composable
@Preview
private fun DefaultPreview() {
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

@Composable
@Preview
private fun DiffPreview() {
    SimpleProgressIndicator(
        modifier = Modifier
            .width(100.dp)
            .height(10.dp),
        progress = 0.7f,
        progressBarColor = Color.Red,
        cornerRadius = 8.dp,
        trackColor = Color(0XFFFBE8E8),
        innerProgressBarPadding = 2.dp,
        diffConfig = DiffConfig(
            lastProgress = 0.5f,
            diffColor = Color.Green,
            showPercent = false
        )
    )
}

@Composable
@Preview
private fun ProgressOnDiffPreview() {
    SimpleProgressIndicator(
        modifier = Modifier
            .width(100.dp)
            .height(10.dp),
        progress = 0.7f,
        progressBarColor = Color.Red,
        cornerRadius = 8.dp,
        trackColor = Color(0XFFFBE8E8),
        innerProgressBarPadding = 2.dp,
        diffConfig = DiffConfig(
            lastProgress = 0.5f,
            diffColor = Color.Green,
            showPercent = true
        )
    )
}

@Composable
@Preview
private fun ProgressOnLastProgressPreview() {
    SimpleProgressIndicator(
        modifier = Modifier
            .width(100.dp)
            .height(10.dp),
        progress = 0.6f,
        progressBarColor = Color.Red,
        cornerRadius = 8.dp,
        trackColor = Color(0XFFFBE8E8),
        innerProgressBarPadding = 2.dp,
        diffConfig = DiffConfig(
            lastProgress = 0.5f,
            diffColor = Color.Green,
            showPercent = true
        )
    )
}

@Composable
@Preview
private fun ProgressOutsidePreview() {
    SimpleProgressIndicator(
        modifier = Modifier
            .width(100.dp)
            .height(10.dp),
        progress = 0.2f,
        progressBarColor = Color.Red,
        cornerRadius = 8.dp,
        trackColor = Color(0XFFFBE8E8),
        innerProgressBarPadding = 2.dp,
        diffConfig = DiffConfig(
            lastProgress = 0.1f,
            diffColor = Color.Green,
            showPercent = true
        )
    )
}
