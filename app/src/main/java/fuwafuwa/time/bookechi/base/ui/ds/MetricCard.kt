package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Карточка метрики: surfaceElevated + stroke.
 * Крупное число serif (headlineMedium) + подпись bodyMedium textSecondary.
 * Если задан [onClick] — карточка кликабельна и показывает шеврон-подсказку в углу.
 */
@Composable
fun MetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = modifier
            .clip(DsShapes.card)
            .background(colors.surfaceElevated, DsShapes.card)
            .border(BorderStroke(1.dp, colors.stroke), DsShapes.card)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(Spacing.lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
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
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(colors.accentSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colors.accentDeep,
                    modifier = Modifier.size(15.dp),
                )
            }
        }
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
