package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import fuwafuwa.time.bookechi.base.ui.ds.FilterChip
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

private val ReminderTimes = listOf("20:00", "20:30", "21:00", "21:30", "22:00")

/** Мини-шторка настройки напоминания о чтении (макет «26»). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderBottomSheet(
    enabled: Boolean,
    time: String,
    onToggle: (Boolean) -> Unit,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val sheetState = rememberModalBottomSheetState()
    var showTimePicker by remember { mutableStateOf(false) }
    val isCustom = time !in ReminderTimes

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.stroke) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxl)
                .padding(bottom = Spacing.xxxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text(
                text = "Напоминание о чтении",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
            )
            Text(
                text = "Вечером, когда дела стихают, — самое время для пары глав. " +
                    "Напомним мягко, один раз в день.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Напоминать каждый день",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.accent,
                        checkedBorderColor = colors.accent,
                        uncheckedThumbColor = colors.textSecondary,
                        uncheckedTrackColor = colors.chipBg,
                        uncheckedBorderColor = colors.stroke,
                    ),
                )
            }

            SectionLabel(text = "Время")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                ReminderTimes.forEach { t ->
                    FilterChip(
                        text = t,
                        selected = t == time,
                        onClick = { onTimeSelected(t) },
                    )
                }
                FilterChip(
                    text = if (isCustom) time else "Своё…",
                    selected = isCustom,
                    onClick = { showTimePicker = true },
                )
            }

            Text(
                text = if (enabled) "Хорошо: в $time напомним о чтении."
                else "Напоминания выключены.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )

            PrimaryButton(
                text = "Готово",
                onClick = onDismiss,
            )
        }
    }

    if (showTimePicker) {
        ReminderTimePickerDialog(
            initialTime = time,
            onConfirm = {
                onTimeSelected(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePickerDialog(
    initialTime: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val parts = initialTime.split(":")
    val initialHour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 21
    val initialMinute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
    val pickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        title = {
            Text(
                text = "Своё время",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
            )
        },
        text = {
            TimePicker(state = pickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                val formatted = "%02d:%02d".format(pickerState.hour, pickerState.minute)
                onConfirm(formatted)
            }) {
                Text(text = "Готово", color = colors.accentDeep)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена", color = colors.textSecondary)
            }
        },
    )
}
