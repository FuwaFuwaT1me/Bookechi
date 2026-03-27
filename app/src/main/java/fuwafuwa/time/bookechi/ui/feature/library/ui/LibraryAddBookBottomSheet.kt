package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.NewBookCover
import fuwafuwa.time.bookechi.base.ui.util.BookechiBottomSheetScaffold
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryAction
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaLibraryBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
fun BoxScope.LibraryAddBookBottomSheet(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit
) {
    val draft = state.addingBook ?: return
    val canSave = draft.name.isNotBlank() && !draft.isSaving && !draft.isCoverLoading

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onAction(LibraryAction.LoadAddingBookCover(uri))
    }

    BookechiBottomSheetScaffold(
        onDismissRequest = { onAction(LibraryAction.CancelAddingBook) },
        containerColor = FigmaLibraryBackground,
        header = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Добавить книгу",
                    color = FigmaTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = { onAction(LibraryAction.SaveAddingBook) },
                    enabled = canSave
                ) {
                    if (draft.isSaving || draft.isCoverLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Сохранить",
                            color = FigmaTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Обложка",
                                color = FigmaTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Нажмите, чтобы выбрать",
                                color = FigmaSubtitle,
                                fontSize = 12.sp
                            )
                        }

                        TextButton(
                            onClick = { coverPickerLauncher.launch("image/*") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = FigmaTitle,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Сменить",
                                color = FigmaTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        NewBookCover(
                            modifier = Modifier
                                .height(170.dp)
                                .width(120.dp),
                            imageUri = draft.coverPath?.toUri(),
                            onClick = { coverPickerLauncher.launch("image/*") }
                        )
                    }

                    if (draft.coverPath != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { onAction(LibraryAction.ClearAddingBookCover) }
                            ) {
                                Text(
                                    text = "Удалить обложку",
                                    color = FigmaSubtitle,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    AddBookTextField(
                        value = draft.name,
                        onValueChange = { onAction(LibraryAction.UpdateAddingBookName(it)) },
                        label = "Название книги",
                        placeholder = "Введите название"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AddBookTextField(
                        value = draft.author,
                        onValueChange = { onAction(LibraryAction.UpdateAddingBookAuthor(it)) },
                        label = "Автор",
                        placeholder = "Введите автора"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAction(
                                    LibraryAction.UpdateAddingBookReadingNow(!draft.readingNow)
                                )
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Читаю сейчас",
                                color = FigmaTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Включите, чтобы указать текущую страницу",
                                color = FigmaSubtitle,
                                fontSize = 12.sp
                            )
                        }

                        Switch(
                            checked = draft.readingNow,
                            onCheckedChange = {
                                onAction(LibraryAction.UpdateAddingBookReadingNow(it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = FigmaTitle
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (draft.readingNow) {
                            AddBookTextField(
                                value = draft.currentPage.toTextFieldValue(),
                                onValueChange = {
                                    onAction(
                                        LibraryAction.UpdateAddingBookCurrentPage(it.toIntOrZero())
                                    )
                                },
                                label = "Текущая",
                                placeholder = "0",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        AddBookTextField(
                            value = draft.pages.toTextFieldValue(),
                            onValueChange = {
                                onAction(
                                    LibraryAction.UpdateAddingBookAllPages(it.toIntOrZero())
                                )
                            },
                            label = "Всего страниц",
                            placeholder = "0",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (draft.error != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text(
                    text = draft.error,
                    color = FigmaSubtitle,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AddBookTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = FigmaSubtitle,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = FigmaSubtitle.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FigmaTitle,
                unfocusedBorderColor = FigmaBackground,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

private fun Int.toTextFieldValue(): String = if (this > 0) this.toString() else ""

private fun String.toIntOrZero(): Int = toIntOrNull()?.coerceAtLeast(0) ?: 0
