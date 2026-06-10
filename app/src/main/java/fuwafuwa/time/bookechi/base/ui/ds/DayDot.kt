package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Состояние дня в мини-полоске стрика. */
enum class DayState { Done, Today, Empty }

/**
 * Столбик дня: буква дня (labelSmall) сверху и кружок 28.dp снизу.
 * Done — фон accentDeep + белая галочка; Today — обводка accent 2.dp;
 * Empty — пунктирная обводка stroke.
 */
@Composable
fun DayDot(
    label: String,
    state: DayState,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs + 2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                DayState.Done -> {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(colors.accentDeep, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                DayState.Today -> {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(2.dp, colors.accent, CircleShape),
                    )
                }
                DayState.Empty -> {
                    val stroke = colors.stroke
                    Canvas(modifier = Modifier.size(28.dp)) {
                        drawCircle(
                            color = stroke,
                            radius = size.minDimension / 2f - 1f,
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    intervals = floatArrayOf(4f, 4f),
                                    phase = 0f,
                                ),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "DayDot Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun DayDotPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                DayDot(label = "Пн", state = DayState.Done)
                DayDot(label = "Вт", state = DayState.Done)
                DayDot(label = "Ср", state = DayState.Today)
                DayDot(label = "Чт", state = DayState.Empty)
            }
        }
    }
}

@Preview(name = "DayDot Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun DayDotPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                DayDot(label = "Пн", state = DayState.Done)
                DayDot(label = "Вт", state = DayState.Today)
                DayDot(label = "Ср", state = DayState.Empty)
            }
        }
    }
}
