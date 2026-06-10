package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Оценка книги: 5 звёзд (filled / border), заполненные accent.
 * Если onRate != null — звёзды кликабельны (передаётся 1..5).
 */
@Composable
fun RatingStars(
    rating: Int,
    modifier: Modifier = Modifier,
    onRate: ((Int) -> Unit)? = null,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        for (i in 1..5) {
            val filled = i <= rating
            val starModifier = if (onRate != null) {
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onRate(i) }
            } else {
                Modifier.size(28.dp)
            }
            Icon(
                imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = colors.accent,
                modifier = starModifier,
            )
        }
    }
}

@Preview(name = "RatingStars Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun RatingStarsPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                RatingStars(rating = 5)
                RatingStars(rating = 3, onRate = {})
                RatingStars(rating = 0, onRate = {})
            }
        }
    }
}

@Preview(name = "RatingStars Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun RatingStarsPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                RatingStars(rating = 4)
                RatingStars(rating = 2, onRate = {})
            }
        }
    }
}
