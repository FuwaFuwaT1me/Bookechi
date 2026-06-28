package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Тёплый «почти-белый» цвет текста/иконок на терракотовых поверхностях (--on-accent). */
@Composable
fun authOnAccent(): Color = if (BookechiTheme.colors.isDark) Color(0xFFFFF1E6) else Color(0xFFFFF6EE)

/* ── Официальный 4-цветный логотип Google «G» ── */
private val GoogleGVector: ImageVector by lazy {
    ImageVector.Builder(
        name = "GoogleG",
        defaultWidth = 48.dp, defaultHeight = 48.dp,
        viewportWidth = 48f, viewportHeight = 48f,
    ).apply {
        addPath(
            addPathNodes("M43.6 20.5H42V20H24v8h11.3c-1.6 4.7-6.1 8-11.3 8-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.9 1.2 8 3.1l5.7-5.7C34.6 6.1 29.6 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.1-2.6-.4-3.5z"),
            fill = SolidColor(Color(0xFFFFC107)),
        )
        addPath(
            addPathNodes("M6.3 14.7l6.6 4.8C14.7 16 19 12 24 12c3.1 0 5.9 1.2 8 3.1l5.7-5.7C34.6 6.1 29.6 4 24 4 16.3 4 9.7 8.3 6.3 14.7z"),
            fill = SolidColor(Color(0xFFFF3D00)),
        )
        addPath(
            addPathNodes("M24 44c5.5 0 10.5-2.1 14.3-5.5l-6.6-5.6C29.7 34.6 27 36 24 36c-5.2 0-9.6-3.3-11.3-7.9l-6.5 5C9.6 39.6 16.2 44 24 44z"),
            fill = SolidColor(Color(0xFF4CAF50)),
        )
        addPath(
            addPathNodes("M43.6 20.5H42V20H24v8h11.3c-.8 2.3-2.3 4.3-4.3 5.7l6.6 5.6C39.9 38.5 44 32 44 24c0-1.3-.1-2.6-.4-3.5z"),
            fill = SolidColor(Color(0xFF1976D2)),
        )
    }.build()
}

@Composable
fun GoogleG(size: Dp) {
    Image(
        painter = rememberVectorPainter(GoogleGVector),
        contentDescription = null,
        modifier = Modifier.size(size),
    )
}

/* ── Глиф «стопка книг» (вордмарка) ── */
@Composable
fun StackGlyph(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension / 40f
        val w = 2.4f * s
        fun book(x: Float, y: Float, bw: Float, bh: Float) {
            drawRoundRect(
                color = color,
                topLeft = Offset(x * s, y * s),
                size = Size(bw * s, bh * s),
                cornerRadius = CornerRadius(2.4f * s, 2.4f * s),
                style = Stroke(width = w),
            )
        }
        book(8.5f, 24.5f, 23f, 7f)
        rotate(-5f, pivot = Offset(20f * s, 20f * s)) { book(10f, 16.6f, 21f, 7f) }
        rotate(4f, pivot = Offset(20f * s, 12f * s)) { book(9.5f, 8.8f, 22f, 7f) }
        // «странички» — короткие штрихи
        drawLine(color.copy(alpha = 0.6f), Offset(13f * s, 28f * s), Offset(17f * s, 28f * s), strokeWidth = 1.6f * s, cap = StrokeCap.Round)
        rotate(-5f, pivot = Offset(20f * s, 20f * s)) {
            drawLine(color.copy(alpha = 0.6f), Offset(14.3f * s, 20.2f * s), Offset(18.1f * s, 19.9f * s), strokeWidth = 1.6f * s, cap = StrokeCap.Round)
        }
    }
}

/** Плитка-марка приложения: скруглённый терракотовый квадрат с глифом стопки книг. */
@Composable
fun BrandMark(size: Dp, modifier: Modifier = Modifier) {
    val colors = BookechiTheme.colors
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(percent = 28))
            .background(Brush.linearGradient(listOf(colors.accent, colors.accentDeep))),
        contentAlignment = Alignment.Center,
    ) {
        StackGlyph(size = size * 0.6f, color = authOnAccent())
    }
}
