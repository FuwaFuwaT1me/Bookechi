package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Лёгкое сжатие при нажатии (как `:active { transform: scale(.98) }` в референсе). */
@Composable
private fun Modifier.pressScale(source: MutableInteractionSource, pressedScale: Float = 0.98f): Modifier {
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) pressedScale else 1f, label = "pressScale")
    return this.graphicsLayer { scaleX = scale; scaleY = scale }
}

/** Главная кнопка Google (filled-terracotta): белый чип с «G» слева + центрированная подпись. */
@Composable
fun GoogleMethodButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    val colors = BookechiTheme.colors
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .pressScale(source)
            .clip(DsShapes.button)
            .background(colors.accent)
            .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            GoogleG(18.dp)
        }
        Text(text = text, color = authOnAccent(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Вторичная кнопка-провайдер (outline): иконка слева + центрированная подпись. */
@Composable
fun OutlineMethodButton(
    text: String,
    leading: @Composable () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = BookechiTheme.colors
    val source = remember { MutableInteractionSource() }
    val bg = if (colors.isDark) colors.surface else colors.surfaceElevated
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .pressScale(source)
            .clip(DsShapes.button)
            .background(bg)
            .border(1.5.dp, colors.stroke, DsShapes.button)
            .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 18.dp)) { leading() }
        Text(text = text, color = colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Кнопка «Войти по почте» (outline + конверт). */
@Composable
fun EmailMethodButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    val colors = BookechiTheme.colors
    OutlineMethodButton(
        text = text,
        leading = { Icon(Icons.Outlined.MailOutline, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(20.dp)) },
        onClick = onClick,
        enabled = enabled,
    )
}

/** Кнопка «Продолжить с Google» в outline-варианте (для формы почты). */
@Composable
fun GoogleOutlineButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    OutlineMethodButton(
        text = text,
        leading = { GoogleG(20.dp) },
        onClick = onClick,
        enabled = enabled,
    )
}

/** Третичная текстовая кнопка (анонимно): подпись + шеврон, сдвигающийся при нажатии. */
@Composable
fun AnonMethodButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    val colors = BookechiTheme.colors
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val chevShift by animateFloatAsState(if (pressed) 3f else 0f, label = "chev")
    val color = if (pressed) colors.textPrimary else colors.textSecondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 46.dp)
            .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, color = color, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .padding(start = 7.dp)
                .size(17.dp)
                .graphicsLayer { translationX = chevShift.dp.toPx() },
        )
    }
}

/** Главная filled-кнопка (терракота) со спиннером во время запроса. */
@Composable
fun AuthFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    busy: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = BookechiTheme.colors
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .pressScale(source)
            .clip(DsShapes.button)
            .background(if (enabled) colors.accent else colors.accentSoft)
            .clickable(interactionSource = source, indication = null, enabled = enabled && !busy, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (busy) {
            AuthSpinner(size = 22.dp, color = authOnAccent(), strokeWidth = 2.5.dp)
        } else {
            Text(text = text, color = authOnAccent(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Вторичная кнопка (bk-btn-secondary): прозрачный фон, акцентная рамка и текст. */
@Composable
fun AuthSecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val colors = BookechiTheme.colors
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .pressScale(source)
            .clip(DsShapes.button)
            .border(1.5.dp, colors.accentSoft, DsShapes.button)
            .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = colors.accentDeep, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Текстовая ссылка-кнопка (акцентная). */
@Composable
fun AuthTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = BookechiTheme.colors.accentDeep,
) {
    val source = remember { MutableInteractionSource() }
    Text(
        text = text,
        color = color,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.clickable(interactionSource = source, indication = null, onClick = onClick),
    )
}

/** Разделитель с лейблом «или». */
@Composable
fun AuthOrDivider(modifier: Modifier = Modifier) {
    val colors = BookechiTheme.colors
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(colors.divider))
        Text(
            text = "или",
            color = colors.textSecondary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        Box(Modifier.weight(1f).height(1.dp).background(colors.divider))
    }
}

/** Мелкий легальный текст со «ссылками» (выделены акцентом, без реальных переходов). */
@Composable
fun AuthLegal(
    prefix: String = "Продолжая, вы соглашаетесь с ",
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Text(
        text = buildAuthLegal(prefix, colors.accentDeep, colors.textSecondary),
        fontSize = 12.sp,
        lineHeight = 18.sp,
        color = colors.textSecondary,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth(),
    )
}

private fun buildAuthLegal(prefix: String, link: Color, base: Color) =
    androidx.compose.ui.text.buildAnnotatedString {
        append(prefix)
        pushStyle(androidx.compose.ui.text.SpanStyle(color = link, fontWeight = FontWeight.SemiBold))
        append("Условиями")
        pop()
        append(" и ")
        pushStyle(androidx.compose.ui.text.SpanStyle(color = link, fontWeight = FontWeight.SemiBold))
        append("Политикой конфиденциальности")
        pop()
        append(".")
    }

/** Вращающийся спиннер: тусклое кольцо + яркая дуга сверху. */
@Composable
fun AuthSpinner(size: Dp, color: Color, strokeWidth: Dp, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "spin")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Restart),
        label = "angle",
    )
    Canvas(modifier = modifier.size(size)) {
        val sw = strokeWidth.toPx()
        val inset = sw / 2f
        val arcSize = Size(this.size.width - sw, this.size.height - sw)
        drawCircle(color = color.copy(alpha = 0.3f), radius = (this.size.minDimension - sw) / 2f, style = Stroke(sw))
        rotate(angle) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = sw, cap = StrokeCap.Round),
            )
        }
    }
}
