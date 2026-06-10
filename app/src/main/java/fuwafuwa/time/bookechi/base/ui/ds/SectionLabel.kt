package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Eyebrow-лейбл секции КАПСОМ: labelSmall, цвет textSecondary.
 * Текст приводится к верхнему регистру.
 */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = colors.textSecondary,
        modifier = modifier,
    )
}

@Preview(name = "SectionLabel Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun SectionLabelPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            SectionLabel(text = "Ещё в чтении и планах", modifier = Modifier.padding(Spacing.lg))
        }
    }
}

@Preview(name = "SectionLabel Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun SectionLabelPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            SectionLabel(text = "История чтения", modifier = Modifier.padding(Spacing.lg))
        }
    }
}
