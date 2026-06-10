package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Карточка метрики: surfaceElevated + stroke.
 * Крупное число serif (headlineMedium) + подпись bodyMedium textSecondary.
 */
@Composable
fun MetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = modifier
            .background(colors.surfaceElevated, DsShapes.card)
            .border(BorderStroke(1.dp, colors.stroke), DsShapes.card)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
    }
}

@Preview(name = "MetricCard Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun MetricCardPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                MetricCard(value = "12", label = "Книг прочитано", modifier = Modifier.weight(1f))
                MetricCard(value = "3 480", label = "Страниц прочитано", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(name = "MetricCard Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun MetricCardPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                MetricCard(value = "7", label = "Дней без перерывов", modifier = Modifier.weight(1f))
                MetricCard(value = "48", label = "Стр./день в среднем", modifier = Modifier.weight(1f))
            }
        }
    }
}
