package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Скруглённый прогресс-бар: трек фон stroke, заполнение accent, clip CircleShape.
 * @param progress доля заполнения 0f..1f (значения вне диапазона обрезаются).
 */
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    val colors = BookechiTheme.colors
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(colors.stroke),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(colors.accent),
        )
    }
}

@Preview(name = "ProgressBar Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun ProgressBarPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                ProgressBar(progress = 0.85f)
                ProgressBar(progress = 0.3f, height = 12.dp)
            }
        }
    }
}

@Preview(name = "ProgressBar Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun ProgressBarPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                ProgressBar(progress = 0.85f)
                ProgressBar(progress = 0.3f, height = 12.dp)
            }
        }
    }
}
