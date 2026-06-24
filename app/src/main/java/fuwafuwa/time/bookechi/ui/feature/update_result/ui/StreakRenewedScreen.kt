package fuwafuwa.time.bookechi.ui.feature.update_result.ui

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.data.repository.WeekStreakDay
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import java.time.LocalDate
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val TOTAL_MS = 4000f

private val StreakBgTop = Color(0xFFBE5E3B)
private val StreakBgBottom = Color(0xFF843B22)
private val StreakOn = Color(0xFFFFF6EE)
private val StreakCheck = Color(0xFF9E4A2C)

private val WeekdayLabelRes = listOf(
    R.string.home_weekday_mon,
    R.string.home_weekday_tue,
    R.string.home_weekday_wed,
    R.string.home_weekday_thu,
    R.string.home_weekday_fri,
    R.string.home_weekday_sat,
    R.string.home_weekday_sun,
)

private fun phase(t: Float, start: Float, end: Float): Float =
    ((t - start) / (end - start)).coerceIn(0f, 1f)

/**
 * Полноэкранная перебивка «Серия продлена»: яркое празднование продления серии.
 * Один таймлайн прогресса [t] в мс (Animatable), из него считаются фазы каждого
 * элемента. Авто-переход к результатам через ~2.45 c или по тапу. При reduce-motion
 * показывается финальное состояние без движения.
 */
@Composable
fun StreakRenewedScreen(
    streak: Int,
    prevStreak: Int,
    days: List<WeekStreakDay>,
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    val reduceMotion = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }

    val t = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        if (reduceMotion) {
            t.snapTo(TOTAL_MS)
        } else {
            t.animateTo(TOTAL_MS, animationSpec = tween(TOTAL_MS.toInt(), easing = LinearEasing))
        }
    }

    // Переход только по тапу (без авто-перехода). Защита от двойного вызова.
    var done by remember { mutableStateOf(false) }
    val goOnce: () -> Unit = {
        if (!done) {
            done = true
            onContinue()
        }
    }

    StreakRenewedContent(
        progressMs = t.value,
        streak = streak,
        prevStreak = prevStreak,
        days = days,
        onTap = goOnce,
    )
}

@Composable
private fun StreakRenewedContent(
    progressMs: Float,
    streak: Int,
    prevStreak: Int,
    days: List<WeekStreakDay>,
    onTap: () -> Unit,
) {
    val density = LocalDensity.current
    val tv = progressMs

    // Фазы элементов (единый таймлайн tv в мс).
    val flameScale = EaseOutBack.transform(phase(tv, 150f, 800f))

    // Число: проявление группы 550–950 (opacity + scale 0.7→1).
    val groupReveal = EaseOutCubic.transform(phase(tv, 550f, 950f))
    val groupAlpha = groupReveal
    val groupBaseScale = 0.7f + 0.3f * groupReveal
    // «Барабанный» перекат 1250–2200 (easeInOutCubic): колонка [prev, new] едет вверх.
    val rollP = EaseInOutCubic.transform(phase(tv, 1250f, 2200f))
    // Bounce приземления 2050–2400 (одиночный sin-горб ±6%).
    val bounceScale = 1f + 0.06f * sin(phase(tv, 2050f, 2400f) * Math.PI.toFloat())
    val numberScale = groupBaseScale * bounceScale
    // Чип «+1»: scale 1500–2050 (easeOutBack), дальше остаётся на месте.
    val chipScale = EaseOutBack.transform(phase(tv, 1500f, 2050f))
    val chipAlpha = phase(tv, 1500f, 1800f)

    // Заголовок/подзаголовок 2150–2650 (въезд снизу + проявление).
    val subP = EaseOutCubic.transform(phase(tv, 2150f, 2650f))
    val subOffsetPx = (1f - subP) * with(density) { 12.dp.toPx() }

    val hintAlpha = phase(tv, 3300f, 3800f)

    val numberHeight = 104.dp
    val numberHeightPx = with(density) { numberHeight.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(StreakBgTop, StreakBgBottom),
                        center = Offset(size.width / 2f, size.height * 0.38f),
                        radius = size.maxDimension * 0.9f,
                    ),
                )
            }
            .pointerInput(Unit) { detectTapGestures { onTap() } },
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Пламя + лучи.
            Box(
                modifier = Modifier.size(230.dp),
                contentAlignment = Alignment.Center,
            ) {
                Rays(progress = tv)
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer { scaleX = flameScale; scaleY = flameScale }
                        .clip(RoundedCornerShape(44.dp))
                        .background(StreakOn.copy(alpha = 0.16f))
                        .border(1.dp, StreakOn.copy(alpha = 0.3f), RoundedCornerShape(44.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = StreakOn,
                        modifier = Modifier.size(74.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Число серии: «барабанный» перекат [prevStreak → streak].
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .height(numberHeight)
                        .graphicsLayer {
                            scaleX = numberScale
                            scaleY = numberScale
                            alpha = groupAlpha
                        }
                        .clipToBounds(),
                    contentAlignment = Alignment.Center,
                ) {
                    // Оба числа — отдельные дети окна, каждое центрировано (как
                    // одиночное число в оригинале). Перекат — чисто draw-time сдвиг:
                    // prev уезжает вверх, streak приезжает снизу. Без Column —
                    // нечему «схлопывать» высоту второго числа.
                    RollNumber(
                        value = prevStreak,
                        height = numberHeight,
                        modifier = Modifier.graphicsLayer { translationY = -rollP * numberHeightPx },
                    )
                    RollNumber(
                        value = streak,
                        height = numberHeight,
                        modifier = Modifier.graphicsLayer { translationY = (1f - rollP) * numberHeightPx },
                    )
                }
                // Чип «+1» справа-сверху от числа.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = 10.dp)
                        .graphicsLayer {
                            scaleX = chipScale
                            scaleY = chipScale
                            alpha = chipAlpha
                        }
                        .clip(CircleShape)
                        .background(StreakOn)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "+1",
                        style = MaterialTheme.typography.labelLarge,
                        color = StreakBgBottom,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Text(
                text = pluralStringResource(R.plurals.streak_intro_days, streak),
                style = MaterialTheme.typography.headlineLarge,
                color = StreakOn,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    translationY = subOffsetPx
                    alpha = subP
                },
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.streak_intro_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = StreakOn.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    translationY = subOffsetPx
                    alpha = subP
                },
            )

            Spacer(Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0..6) {
                    val day = days.getOrNull(i)
                    val marked = day?.isStreakDay == true
                    val isToday = day?.isToday == true
                    // Дни зажигаются ПОСЛЕ переключения счётчика, по очереди.
                    val appearStart = 2350f + i * 110f
                    val p = phase(tv, appearStart, appearStart + 420f)
                    val dotScale = EaseOutBack.transform(p)
                    val dotAlpha = phase(tv, appearStart, appearStart + 300f)
                    val dotOffsetPx = (1f - EaseOutCubic.transform(p)) * with(density) { 8.dp.toPx() }
                    DayCell(
                        label = stringResource(WeekdayLabelRes[i]),
                        marked = marked,
                        isToday = isToday,
                        scale = dotScale,
                        alpha = dotAlpha,
                        offsetYpx = dotOffsetPx,
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.streak_intro_tap),
            style = MaterialTheme.typography.bodyMedium,
            color = StreakOn.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .graphicsLayer { alpha = hintAlpha },
        )
    }
}

@Composable
private fun DayCell(
    label: String,
    marked: Boolean,
    isToday: Boolean,
    scale: Float,
    alpha: Float,
    offsetYpx: Float,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            translationY = offsetYpx
        },
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = StreakOn.copy(alpha = if (isToday) 1f else 0.85f),
            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale },
            contentAlignment = Alignment.Center,
        ) {
            // Кольцо-обводка + свечение для сегодняшнего дня.
            if (isToday) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val c = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(StreakOn.copy(alpha = 0.25f), radius = 18.dp.toPx(), center = c)
                    drawCircle(StreakOn, radius = 17.dp.toPx(), style = Stroke(2.dp.toPx()), center = c)
                    drawCircle(StreakBgBottom, radius = 15.dp.toPx(), style = Stroke(2.5.dp.toPx()), center = c)
                }
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .then(
                        if (marked) {
                            Modifier.background(StreakOn)
                        } else {
                            Modifier.border(1.dp, StreakOn.copy(alpha = 0.5f), CircleShape)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (marked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = StreakCheck,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        if (isToday) {
            Spacer(Modifier.height(3.dp))
            Text(
                text = stringResource(R.string.streak_intro_today).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = StreakOn,
            )
        }
    }
}

@Composable
private fun RollNumber(value: Int, height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(height),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 96.sp,
                lineHeight = 100.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = StreakOn,
        )
    }
}

/** 12 тонких лучей-вспышек вокруг пламени, выстреливают со стэггером и гаснут. */
@Composable
private fun Rays(progress: Float) {
    val rayColor = StreakOn
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r0 = 78.dp.toPx()
        val maxLen = 56.dp.toPx()
        val width = 3.dp.toPx()
        for (i in 0 until 12) {
            val start = 200f + i * 18f
            val grow = EaseOutCubic.transform(phase(progress, start, start + 300f))
            val a = 0.5f * grow
            if (a <= 0.01f) continue
            val angle = (i * (360f / 12f)) * (Math.PI.toFloat() / 180f)
            val dir = Offset(cos(angle), sin(angle))
            val p1 = center + dir * r0
            val p2 = center + dir * (r0 + maxLen * grow)
            drawLine(
                color = rayColor.copy(alpha = a),
                start = p1,
                end = p2,
                strokeWidth = width,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Preview(name = "Серия продлена", showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun StreakRenewedPreview() {
    BookechiTheme {
        val days = (0..6).map { i ->
            WeekStreakDay(
                date = LocalDate.of(2026, 6, 15).plusDays(i.toLong()),
                isStreakDay = i < 2,
                isToday = i == 1,
            )
        }
        StreakRenewedContent(
            progressMs = TOTAL_MS,
            streak = 8,
            prevStreak = 7,
            days = days,
            onTap = {},
        )
    }
}
