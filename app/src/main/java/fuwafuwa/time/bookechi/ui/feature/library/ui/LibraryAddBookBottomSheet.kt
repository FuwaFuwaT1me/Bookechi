package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun BoxScope.LibraryAddBookBottomSheet(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit
) {
    val draft = state.addingBook ?: return
    val colors = BookechiTheme.colors

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onAction(LibraryAction.LoadAddingBookCover(uri))
    }

    BookechiBottomSheetScaffold(
        onDismissRequest = { onAction(LibraryAction.CancelAddingBook) },
        containerColor = colors.canvas,
        header = {
            Text(
                text = "Добавить книгу",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.md),
            )
        }
    ) {
        item {
            CoverTile(
                coverPath = draft.coverPath,
                title = draft.name,
                author = draft.author,
                isLoading = draft.isCoverLoading,
                onPick = { coverPickerLauncher.launch("image/*") },
                onClear = if (draft.coverPath != null) {
                    { onAction(LibraryAction.ClearAddingBookCover) }
                } else {
                    null
                },
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            Column {
                WarmTextField(
                    value = draft.name,
                    onValueChange = { onAction(LibraryAction.UpdateAddingBookName(it)) },
                    label = "Название",
                    placeholder = "Например, «Норвежский лес»",
                )
                if (draft.nameError) {
                    FieldError(text = "Введите название книги")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.md)) }

        item {
            WarmTextField(
                value = draft.author,
                onValueChange = { onAction(LibraryAction.UpdateAddingBookAuthor(it)) },
                label = "Автор",
                placeholder = "Имя автора",
            )
        }

        item { Spacer(modifier = Modifier.height(Spacing.md)) }

        item {
            Column {
                WarmTextField(
                    value = draft.pages,
                    onValueChange = { onAction(LibraryAction.UpdateAddingBookAllPages(it)) },
                    label = "Всего страниц",
                    placeholder = "320",
                    keyboardType = KeyboardType.Number,
                )
                if (draft.pagesError) {
                    FieldError(text = "Укажите количество страниц")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            SectionLabel(text = "Статус", modifier = Modifier.padding(bottom = Spacing.sm))
        }

        item {
            StatusChipsRow(
                selected = draft.status,
                onSelect = { onAction(LibraryAction.UpdateAddingBookStatus(it)) },
            )
        }

        if (draft.status == ReadingStatus.Reading) {
            item { Spacer(modifier = Modifier.height(Spacing.md)) }
            item {
                WarmTextField(
                    value = draft.currentPage,
                    onValueChange = { onAction(LibraryAction.UpdateAddingBookCurrentPage(it)) },
                    label = "Текущая страница",
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                )
            }
        }

        if (draft.error != null) {
            item { Spacer(modifier = Modifier.height(Spacing.md)) }
            item { FieldError(text = draft.error) }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }

        item {
            PrimaryButton(
                text = "Добавить книгу",
                onClick = { onAction(LibraryAction.SaveAddingBook) },
                enabled = !draft.isSaving && !draft.isCoverLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
