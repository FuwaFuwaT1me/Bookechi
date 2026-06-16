package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Инсайт/нудж-плашка (скругление 16): иконка + текст titleSmall.
 * По умолчанию — sageSoft с огоньком (позитивный инсайт). Цвет фона/иконку можно
 * переопределить (например тёплый нудж «ещё не отмечено»).
 */
@Composable
fun InsightPlinth(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BookechiTheme.colors.sageSoft,
    icon: ImageVector = Icons.Default.LocalFireDepartment,
    iconTint: Color = BookechiTheme.colors.accent,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, DsShapes.plinth)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
        )
    }
}

@Preview(name = "InsightPlinth Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun InsightPlinthPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                InsightPlinth(text = "Личный рекорд серии: 8 дней")
            }
        }
    }
}

@Preview(name = "InsightPlinth Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun InsightPlinthPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                InsightPlinth(text = "В июне на 18% больше, чем в мае")
            }
        }
    }
}
