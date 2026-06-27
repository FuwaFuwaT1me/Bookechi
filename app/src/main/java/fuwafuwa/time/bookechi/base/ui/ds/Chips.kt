package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Статус-чип: пилюля на chipBg, текст textSecondary, titleSmall.
 * Используется для статусов «Читаю / В планах / Прочитано».
 */
@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Text(
        text = status,
        style = MaterialTheme.typography.titleSmall,
        color = colors.textSecondary,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(colors.chipBg, CircleShape)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs + 2.dp),
    )
}

/**
 * Фильтр-чип: пилюля.
 * selected → фон accentDeep + белый текст; иначе chipBg + textSecondary.
 */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val bg = if (selected) colors.accentDeep else colors.chipBg
    val fg = if (selected) Color.White else colors.textSecondary
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = fg,
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    )
}

/**
 * Чип быстрого лога: пилюля-outline (surface + stroke, текст accent).
 * Для быстрого добавления страниц «+10 / +25».
 */
@Composable
fun QuickLogChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = colors.accent,
        modifier = modifier
            .clip(CircleShape)
            .background(colors.surface)
            .border(BorderStroke(1.dp, colors.stroke), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    )
}

@Preview(name = "Chips Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun ChipsPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    StatusChip(status = "Читаю")
                    StatusChip(status = "В планах")
                    StatusChip(status = "Прочитано")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FilterChip(text = "Все", selected = true, onClick = {})
                    FilterChip(text = "Читаю", selected = false, onClick = {})
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    QuickLogChip(text = "+10", onClick = {})
                    QuickLogChip(text = "+25", onClick = {})
                }
            }
        }
    }
}

@Preview(name = "Chips Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun ChipsPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    StatusChip(status = "Читаю")
                    StatusChip(status = "Прочитано")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FilterChip(text = "Все", selected = true, onClick = {})
                    FilterChip(text = "Читаю", selected = false, onClick = {})
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    QuickLogChip(text = "+10", onClick = {})
                    QuickLogChip(text = "+25", onClick = {})
                }
            }
        }
    }
}
