package fuwafuwa.time.bookechi.base.ui.ds

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.roundToInt

private val RulerMinuteSpacing = 9.dp

/**
 * Горизонтальная линейка минут: по центру — указатель, линейку тянешь —
 * значение под указателем меняется поминутно. Снизу min 0, вверх не ограничена
 * (тики рисуются динамически вокруг текущего значения). На каждое деление —
 * равномерный тактильный «тик».
 */
@Composable
fun MinutesRuler(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val density = LocalDensity.current
    val pxPerMin = with(density) { RulerMinuteSpacing.toPx() }
    val splineDecay = remember(density) { splineBasedDecay<Float>(density) }

    var posPx by remember { mutableFloatStateOf(value.toFloat() * pxPerMin) }

    val onValueChangeUpdated = rememberUpdatedState(onValueChange)
    val view = LocalView.current
    LaunchedEffect(pxPerMin) {
        var first = true
        snapshotFlow { (posPx / pxPerMin).roundToInt().coerceAtLeast(0) }
            .distinctUntilChanged()
            .collect { minute ->
                onValueChangeUpdated.value(minute)
                if (first) {
                    first = false
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            }
    }

    val scrollState = rememberScrollableState { delta ->
        val old = posPx
        posPx = (old + delta).coerceAtLeast(0f)
        posPx - old
    }
    val flingBehavior = remember(splineDecay, pxPerMin) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                var velocityLeft = initialVelocity
                if (kotlin.math.abs(initialVelocity) > 1f) {
                    var last = 0f
                    AnimationState(initialValue = 0f, initialVelocity = initialVelocity)
                        .animateDecay(splineDecay) {
                            val delta = this.value - last
                            val consumed = scrollBy(delta)
                            last = this.value
                            velocityLeft = this.velocity
                            if (kotlin.math.abs(delta - consumed) > 0.5f) cancelAnimation()
                        }
                }
                val target = (posPx / pxPerMin).roundToInt().coerceAtLeast(0) * pxPerMin
                var lastSnap = 0f
                animate(0f, target - posPx, animationSpec = tween(durationMillis = 160)) { v, _ ->
                    scrollBy(v - lastSnap)
                    lastSnap = v
                }
                return velocityLeft
            }
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = colors.textSecondary)
    val hourLabels = (0..24).map { stringResource(R.string.update_time_hours, it) }

    val minorColor = colors.textSecondary.copy(alpha = 0.35f)
    val fiveColor = colors.textSecondary.copy(alpha = 0.6f)
    val majorColor = colors.accent
    val pointerColor = colors.accentDeep

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Horizontal,
                reverseDirection = true,
                flingBehavior = flingBehavior,
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val posMin = posPx / pxPerMin
            val topY = size.height * 0.12f
            val minorLen = 12.dp.toPx()
            val fiveLen = 18.dp.toPx()
            val majorLen = 26.dp.toPx()
            val labelY = topY + majorLen + 6.dp.toPx()

            val half = (centerX / pxPerMin).toInt() + 2
            val from = (posMin.toInt() - half).coerceAtLeast(0)
            val to = posMin.toInt() + half
            for (m in from..to) {
                val x = centerX + (m - posMin) * pxPerMin
                if (x < 0f || x > size.width) continue
                val isHour = m % 60 == 0
                val isQuarter = m % 15 == 0
                val isFive = m % 5 == 0
                val len: Float
                val col: Color
                val w: Float
                when {
                    isHour || isQuarter -> {
                        len = majorLen; col = majorColor; w = 2.dp.toPx()
                    }
                    isFive -> {
                        len = fiveLen; col = fiveColor; w = 1.5.dp.toPx()
                    }
                    else -> {
                        len = minorLen; col = minorColor; w = 1.dp.toPx()
                    }
                }
                drawLine(
                    color = col,
                    start = Offset(x, topY),
                    end = Offset(x, topY + len),
                    strokeWidth = w,
                )
                if (isHour || isQuarter) {
                    val label = if (isHour) hourLabels.getOrElse(m / 60) { "${m / 60}" } else "${m % 60}"
                    val layout = textMeasurer.measure(label, labelStyle)
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(x - layout.size.width / 2f, labelY),
                    )
                }
            }

            drawLine(
                color = pointerColor,
                start = Offset(centerX, topY - 4.dp.toPx()),
                end = Offset(centerX, topY + majorLen + 2.dp.toPx()),
                strokeWidth = 2.5.dp.toPx(),
            )
            val triHalf = 5.dp.toPx()
            val triTop = topY - 12.dp.toPx()
            val pointer = Path().apply {
                moveTo(centerX - triHalf, triTop)
                lineTo(centerX + triHalf, triTop)
                lineTo(centerX, triTop + 8.dp.toPx())
                close()
            }
            drawPath(pointer, color = pointerColor)
        }
    }
}
