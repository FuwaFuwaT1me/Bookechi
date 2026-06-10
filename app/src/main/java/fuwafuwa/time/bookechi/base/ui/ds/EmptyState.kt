package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Центрированный пустой экран: тайл-иконка (cardTint + glyph accentDeep),
 * serif-заголовок (headlineSmall), bodyMedium textSecondary и опц. PrimaryButton.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    ctaText: String?,
    onCta: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(colors.cardTint, DsShapes.tile),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.accentDeep,
                modifier = Modifier.size(36.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        if (ctaText != null && onCta != null) {
            PrimaryButton(
                text = ctaText,
                onClick = onCta,
                modifier = Modifier.padding(top = Spacing.sm),
            )
        }
    }
}

@Preview(name = "EmptyState Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun EmptyStatePreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            EmptyState(
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                title = "Добавьте первую книгу",
                subtitle = "Начните отслеживать чтение — здесь появятся ваши книги.",
                ctaText = "Добавить книгу",
                onCta = {},
            )
        }
    }
}

@Preview(name = "EmptyState Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun EmptyStatePreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            EmptyState(
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                title = "Библиотека пуста",
                subtitle = "Здесь появятся ваши книги.",
                ctaText = null,
                onCta = null,
            )
        }
    }
}
