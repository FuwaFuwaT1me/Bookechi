package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.WarmTextField
import fuwafuwa.time.bookechi.base.ui.util.BookechiBottomSheetScaffold
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryAction
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

@Composable
fun BoxScope.EditBookBottomSheet(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit
) {
    val book = state.editingBook ?: return
    val colors = BookechiTheme.colors
    var showDeleteDialog by remember { mutableStateOf(false) }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onAction(LibraryAction.UpdateBook(book.copy(coverPath = it.toString())))
        }
    }

    BookechiBottomSheetScaffold(
        onDismissRequest = { onAction(LibraryAction.CancelEditingBook) },
        containerColor = colors.canvas,
        header = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Редактировать книгу",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = if (book.isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = "В избранное",
                    tint = colors.accent,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            onAction(
                                LibraryAction.UpdateBook(book.copy(isFavorite = !book.isFavorite))
                            )
                        },
                )
            }
        }
    ) {
        item {
            CoverTile(
                coverPath = book.coverPath,
                title = book.name,
                author = book.author,
                isLoading = false,
                onPick = { coverPickerLauncher.launch("image/*") },
                onClear = if (book.coverPath != null) {
                    { onAction(LibraryAction.UpdateBook(book.copy(coverPath = null))) }
                } else {
                    null
                },
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            WarmTextField(
                value = book.name,
                onValueChange = { onAction(LibraryAction.UpdateBook(book.copy(name = it))) },
                label = "Название",
                placeholder = "Например, «Норвежский лес»",
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.md)) }

        item {
            WarmTextField(
                value = book.author,
                onValueChange = { onAction(LibraryAction.UpdateBook(book.copy(author = it))) },
                label = "Автор",
                placeholder = "Имя автора",
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.md)) }

        item {
            WarmTextField(
                value = if (book.pages > 0) book.pages.toString() else "",
                onValueChange = {
                    val pages = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
                    val current = book.currentPage.coerceAtMost(if (pages > 0) pages else book.currentPage)
                    onAction(LibraryAction.UpdateBook(book.copy(pages = pages, currentPage = current)))
                },
                label = "Всего страниц",
                placeholder = "320",
                keyboardType = KeyboardType.Number,
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            SectionLabel(text = "Статус", modifier = Modifier.padding(bottom = Spacing.sm))
        }

        item {
            StatusChipsRow(
                selected = book.readingStatus,
                onSelect = { status ->
                    val current = when (status) {
                        ReadingStatus.Completed -> book.pages
                        ReadingStatus.Planned -> 0
                        else -> book.currentPage
                    }
                    onAction(
                        LibraryAction.UpdateBook(
                            book.copy(readingStatus = status, currentPage = current)
                        )
                    )
                },
            )
        }

        if (book.readingStatus == ReadingStatus.Reading) {
            item { Spacer(modifier = Modifier.height(Spacing.md)) }
            item {
                WarmTextField(
                    value = if (book.currentPage > 0) book.currentPage.toString() else "",
                    onValueChange = {
                        val raw = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
                        val clamped = if (book.pages > 0) raw.coerceAtMost(book.pages) else raw
                        onAction(LibraryAction.UpdateBook(book.copy(currentPage = clamped)))
                    },
                    label = "Текущая страница",
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            PrimaryButton(
                text = "Сохранить",
                onClick = { onAction(LibraryAction.CancelEditingBook) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.sm)) }

        item {
            TextButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Удалить книгу",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.accentDeep,
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    text = "Удалить книгу?",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                )
            },
            text = {
                Text(
                    text = "«${book.name}» исчезнет из библиотеки. Это действие необратимо.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onAction(LibraryAction.DeleteBook(book))
                }) {
                    Text(text = "Удалить", color = colors.accentDeep)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Оставить", color = colors.textSecondary)
                }
            },
        )
    }
}
