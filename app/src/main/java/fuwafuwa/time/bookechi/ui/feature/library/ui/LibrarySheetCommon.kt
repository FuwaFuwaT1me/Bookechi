package fuwafuwa.time.bookechi.ui.feature.library.ui

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.FilterChip
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Три статуса, доступных при добавлении/редактировании. */
internal val EditableStatuses: List<Pair<ReadingStatus, String>> = listOf(
    ReadingStatus.Reading to "Читаю",
    ReadingStatus.Planned to "В планах",
    ReadingStatus.Completed to "Прочитано",
)

/**
 * Тёплый тайл выбора обложки: слева предпросмотр обложки, по центру —
 * подпись «Добавить обложку / Фото или из каталога — по желанию», справа —
 * круглая кнопка камеры. Без холодного form-look.
 */
@Composable
internal fun CoverTile(
    coverPath: String?,
    title: String,
    author: String,
    isLoading: Boolean,
    onPick: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardTint, DsShapes.tile)
            .clickable(onClick = onPick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(contentAlignment = Alignment.Center) {
            BookCover(
                coverPath = coverPath,
                title = title.ifBlank { "Обложка" },
                author = author.ifBlank { "" },
                width = 64.dp,
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = colors.accent,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = if (coverPath != null) "Обложка добавлена" else "Добавить обложку",
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
            )
            Text(
                text = "Фото или из каталога — по желанию",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            if (coverPath != null && onClear != null) {
                Text(
                    text = "Убрать обложку",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.accentDeep,
                    modifier = Modifier
                        .padding(top = Spacing.xs)
                        .clickable(onClick = onClear),
                )
            }
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .background(colors.surface, CircleShape)
                .border(BorderStroke(1.dp, colors.stroke), CircleShape)
                .clickable(onClick = onPick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = "Выбрать обложку",
                tint = colors.accent,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/** Ряд из трёх статус-чипов (DS FilterChip). */
@Composable
internal fun StatusChipsRow(
    selected: ReadingStatus,
    onSelect: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        EditableStatuses.forEach { (status, label) ->
            FilterChip(
                text = label,
                selected = selected == status,
                onClick = { onSelect(status) },
            )
        }
    }
}

/** Мягкое сообщение об ошибке под полем (accentDeep). */
@Composable
internal fun FieldError(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = BookechiTheme.colors.accentDeep,
        modifier = modifier.padding(start = Spacing.xs, top = Spacing.xs),
    )
}
