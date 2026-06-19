package fuwafuwa.time.bookechi.ui.feature.book_details.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.WarmTextField
import fuwafuwa.time.bookechi.base.ui.util.BookechiBottomSheetScaffold
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsAction
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsState
import fuwafuwa.time.bookechi.ui.feature.library.ui.CoverTile
import fuwafuwa.time.bookechi.ui.feature.library.ui.StatusChipsRow
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Лист редактирования метаданных книги в деталях. Переиспользует форму из
 * библиотеки (CoverTile / StatusChipsRow / WarmTextField). Правки применяются
 * «вживую» через [BookDetailsAction.UpdateBook]; «Сохранить» просто закрывает лист.
 */
@Composable
fun BoxScope.BookDetailsEditSheet(
    state: BookDetailsState,
    onAction: (BookDetailsAction) -> Unit,
) {
    val book = state.book
    if (!state.isEditing || book == null) return

    val colors = BookechiTheme.colors
    var showDeleteDialog by remember { mutableStateOf(false) }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onAction(BookDetailsAction.UpdateBook(book.copy(coverPath = it.toString())))
        }
    }

    BookechiBottomSheetScaffold(
        onDismissRequest = { onAction(BookDetailsAction.CloseEdit) },
        containerColor = colors.canvas,
        header = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.lib_edit_book),
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
                    contentDescription = stringResource(R.string.lib_add_to_favorites),
                    tint = colors.accent,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            onAction(
                                BookDetailsAction.UpdateBook(book.copy(isFavorite = !book.isFavorite))
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
                    { onAction(BookDetailsAction.UpdateBook(book.copy(coverPath = null))) }
                } else {
                    null
                },
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            WarmTextField(
                value = book.name,
                onValueChange = { onAction(BookDetailsAction.UpdateBook(book.copy(name = it))) },
                label = stringResource(R.string.lib_field_name),
                placeholder = stringResource(R.string.lib_field_name_placeholder),
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.md)) }

        item {
            WarmTextField(
                value = book.author,
                onValueChange = { onAction(BookDetailsAction.UpdateBook(book.copy(author = it))) },
                label = stringResource(R.string.lib_field_author),
                placeholder = stringResource(R.string.lib_field_author_placeholder),
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.md)) }

        item {
            WarmTextField(
                value = if (book.pages > 0) book.pages.toString() else "",
                onValueChange = {
                    val pages = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
                    val current = book.currentPage.coerceAtMost(if (pages > 0) pages else book.currentPage)
                    onAction(BookDetailsAction.UpdateBook(book.copy(pages = pages, currentPage = current)))
                },
                label = stringResource(R.string.lib_field_total_pages),
                placeholder = "320",
                keyboardType = KeyboardType.Number,
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            SectionLabel(
                text = stringResource(R.string.lib_section_status),
                modifier = Modifier.padding(bottom = Spacing.sm),
            )
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
                        BookDetailsAction.UpdateBook(
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
                        onAction(BookDetailsAction.UpdateBook(book.copy(currentPage = clamped)))
                    },
                    label = stringResource(R.string.lib_field_current_page),
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            PrimaryButton(
                text = stringResource(R.string.lib_save),
                onClick = { onAction(BookDetailsAction.CloseEdit) },
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
                    text = stringResource(R.string.lib_delete_book),
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
                    text = stringResource(R.string.lib_delete_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.lib_delete_dialog_message, book.name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onAction(BookDetailsAction.DeleteBook)
                }) {
                    Text(text = stringResource(R.string.lib_delete), color = colors.accentDeep)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.lib_keep), color = colors.textSecondary)
                }
            },
        )
    }
}
