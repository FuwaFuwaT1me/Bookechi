package fuwafuwa.time.bookechi.ui.feature.reading_log.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.MinutesRuler
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.SessionLogItem
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

private val LogError = Color(0xFFA8402A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SessionEditSheet(
    item: SessionLogItem,
    onDismiss: () -> Unit,
    onSave: (toPage: Int, minutes: Int) -> Unit,
    onDelete: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var toText by remember { mutableStateOf(item.toPage.toString()) }
    var mins by remember { mutableIntStateOf(item.minutes) }

    val total = item.totalPages.coerceAtLeast(1)
    val to = (toText.toIntOrNull() ?: item.fromPage).coerceIn(item.fromPage, total)
    val delta = to - item.fromPage
    val percent = (delta * 100f / total).toInt()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.canvas,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
        ) {
            // header
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                BookCover(
                    coverPath = item.coverPath,
                    title = item.title,
                    author = item.author,
                    width = 46.dp,
                    shape = RoundedCornerShape(10.dp),
                )
                Column {
                    Text(
                        text = stringResource(R.string.log_edit_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = "${item.title} · ${"%02d.%02d".format(item.date.dayOfMonth, item.date.monthValue)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = stringResource(R.string.log_edit_pages_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.sm))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                // from (frozen)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.chipBg)
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                ) {
                    Text(stringResource(R.string.log_edit_from), style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    Text(
                        text = item.fromPage.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = colors.accentDeep, modifier = Modifier.size(20.dp))
                // to (editable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceElevated)
                        .border(1.5.dp, colors.accent, RoundedCornerShape(16.dp))
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                ) {
                    Text(stringResource(R.string.log_edit_to), style = MaterialTheme.typography.bodySmall, color = colors.accentDeep)
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        BasicTextField(
                            value = toText,
                            onValueChange = { txt -> toText = txt.filter { it.isDigit() }.take(6) },
                            singleLine = true,
                            cursorBrush = SolidColor(colors.accent),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                            ),
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        Text("/ $total", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                }
            }

            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(R.string.log_edit_delta, delta, percent),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.accentDeep,
            )

            Spacer(Modifier.height(Spacing.lg))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.log_edit_time).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = if (mins > 0) stringResource(R.string.log_time_minutes, mins) else stringResource(R.string.log_edit_time_unset),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (mins > 0) colors.accentDeep else colors.textSecondary,
                )
            }
            MinutesRuler(value = mins, onValueChange = { mins = it })

            Spacer(Modifier.height(Spacing.md))
            PrimaryButton(
                text = stringResource(R.string.log_save),
                onClick = { onSave(to, mins) },
            )
            Spacer(Modifier.height(Spacing.sm))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.log_delete_entry),
                    style = MaterialTheme.typography.titleMedium,
                    color = LogError,
                )
            }
        }
    }
}

@Composable
internal fun SessionDeleteDialog(
    item: SessionLogItem,
    onKeep: () -> Unit,
    onConfirm: () -> Unit,
) {
    val colors = BookechiTheme.colors
    AlertDialog(
        onDismissRequest = onKeep,
        containerColor = colors.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LogError.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = LogError, modifier = Modifier.size(24.dp))
            }
        },
        title = {
            Text(
                text = stringResource(R.string.log_delete_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Text(
                text = stringResource(R.string.log_delete_message, item.title, item.toPage, item.fromPage),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.log_delete), color = LogError, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onKeep) {
                Text(text = stringResource(R.string.log_keep), color = colors.textSecondary)
            }
        },
    )
}
