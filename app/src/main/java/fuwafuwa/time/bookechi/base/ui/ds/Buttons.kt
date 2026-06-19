package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Главная кнопка действия: full-width pill, фон accent, текст onPrimary.
 * Disabled — фон accentSoft.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.accent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = colors.accentSoft,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = Spacing.xxl,
            vertical = Spacing.md,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * Вторичная кнопка: pill, фон surface + 1.dp stroke, текст textPrimary.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.stroke),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colors.surface,
            contentColor = colors.textPrimary,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = Spacing.xxl,
            vertical = Spacing.md,
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(name = "Buttons Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun ButtonsPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                PrimaryButton(text = "Обновить прогресс", onClick = {})
                PrimaryButton(text = "Недоступно", onClick = {}, enabled = false)
                SecondaryButton(text = "Отмена", onClick = {})
            }
        }
    }
}

@Preview(name = "Buttons Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun ButtonsPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                PrimaryButton(text = "Обновить прогресс", onClick = {})
                PrimaryButton(text = "Недоступно", onClick = {}, enabled = false)
                SecondaryButton(text = "Отмена", onClick = {})
            }
        }
    }
}
