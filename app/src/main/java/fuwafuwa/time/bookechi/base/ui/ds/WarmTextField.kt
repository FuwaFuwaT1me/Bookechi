package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * «Тёплое» текстовое поле: OutlinedTextField с заливкой surface (не белый).
 * focusedBorder = accent, unfocused = stroke, текст textPrimary, placeholder textSecondary.
 */
@Composable
fun WarmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val colors = BookechiTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        shape = DsShapes.button,
        label = { Text(text = label) },
        placeholder = {
            Text(text = placeholder, color = colors.textSecondary)
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            disabledContainerColor = colors.surface,
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = colors.stroke,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            cursorColor = colors.accent,
            focusedLabelColor = colors.accent,
            unfocusedLabelColor = colors.textSecondary,
            focusedPlaceholderColor = colors.textSecondary,
            unfocusedPlaceholderColor = colors.textSecondary,
        ),
    )
}

@Preview(name = "WarmTextField Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun WarmTextFieldPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            var name by remember { mutableStateOf("") }
            var pages by remember { mutableStateOf("182") }
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                WarmTextField(value = name, onValueChange = { name = it }, label = "Название", placeholder = "Норвежский лес")
                WarmTextField(
                    value = pages,
                    onValueChange = { pages = it },
                    label = "Всего страниц",
                    placeholder = "320",
                    keyboardType = KeyboardType.Number,
                )
            }
        }
    }
}

@Preview(name = "WarmTextField Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun WarmTextFieldPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            var name by remember { mutableStateOf("Норвежский лес") }
            Column(modifier = Modifier.padding(Spacing.lg)) {
                WarmTextField(value = name, onValueChange = { name = it }, label = "Название", placeholder = "Норвежский лес")
            }
        }
    }
}
