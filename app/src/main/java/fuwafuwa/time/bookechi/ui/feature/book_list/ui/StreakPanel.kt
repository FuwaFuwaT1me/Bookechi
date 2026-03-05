package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.theme.FigmaFire
import fuwafuwa.time.bookechi.ui.theme.FigmaRedTitle
import fuwafuwa.time.bookechi.ui.theme.FigmaStreakBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaStreakBackgroundLight
import fuwafuwa.time.bookechi.ui.theme.FigmaStreakCurrentDayBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
import fuwafuwa.time.bookechi.utils.file.parseWeekDayNumberToShortName

@Composable
fun StreakPanel(
    state: BookListState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White,
                        FigmaStreakBackground
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
//                color = FigmaStreakBackground
            )
            .padding(16.dp)
    ) {

        Text(
            text = "Ваш прогресс",
            color = FigmaRedTitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = "${state.streakDays} дней подряд",
            color = FigmaTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.size(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StreakDay(1, true, false)
            StreakDay(2, true, false)
            StreakDay(3, true, false)
            StreakDay(4, false, true)
            StreakDay(5, false, false)
            StreakDay(6, false, false)
            StreakDay(7, false, false)
        }
    }
}

@Composable
private fun StreakDay(
    dayNumber: Int,
    isStreakDay: Boolean,
    isToday: Boolean,
) {
    val dayName = parseWeekDayNumberToShortName(dayNumber)

    Column(
        modifier = Modifier
            .background(
                color = when {
                    isToday -> FigmaStreakCurrentDayBackground
                    isStreakDay -> Color.White
                    else -> Color.White.copy(alpha = 0.17f)
                },
                shape = RoundedCornerShape(40.dp),
            )
//            .graphicsLayer {
//                shadowElevation = 1.dp.toPx()
//                shape = RoundedCornerShape(40.dp)
//                clip = false
//            }
            .padding(vertical = 5.dp, horizontal = 9.dp)
        ,
    ) {
        when {
            isToday -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = dayName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.size(4.dp))

                Canvas(Modifier.size(24.dp)) {
                    drawCircle(
                        color = Color.White,
                        radius = 10.dp.toPx(),
                        center = Offset(size.width / 2, size.height / 2),
                        style = Stroke(2.dp.toPx())
                    )
                }
            }
            isStreakDay -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = dayName,
                    color = FigmaTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.size(4.dp))

                FlameWithBottomGlow()
            }
            else -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = dayName,
                    color = FigmaTitle.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.size(4.dp))

                Canvas(Modifier.size(24.dp)) {
                    drawCircle(
                        color = FigmaSubtitle,
                        radius = 10.dp.toPx(),
                        center = Offset(size.width / 2, size.height / 2),
                        style = Stroke(
                            2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 9f))
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FlameWithBottomGlow(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            val blurPx = 8.dp.toPx()

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    isAntiAlias = true
                    color = FigmaFire.copy(alpha = 0.65f).toArgb()
                    maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
                }

                // эллипс под огоньком (на нижней части)
                val cx = size.width / 2f
                val cy = size.height * 0.8f
                val rx = size.width * 0.35f
                val ry = size.height * 0.25f

                canvas.nativeCanvas.drawOval(
                    cx - rx, cy - ry,
                    cx + rx, cy + ry,
                    paint
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.fire_5),
            tint = FigmaFire,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun PreviewStreakPanel() {
    StreakPanel(
        state = BookListState(
            books = emptyList(),
            streakDays = 10,
            currentWeekStreakDays = 3
        )
    )
}
