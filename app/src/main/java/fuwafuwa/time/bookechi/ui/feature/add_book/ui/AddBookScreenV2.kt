package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.NewBookCover
import fuwafuwa.time.bookechi.base.ui.util.BookechiBottomSheetScaffold
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookAction
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaLibraryBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
fun AddBookScreenV2(
    viewModel: AddBookViewModel
) {
    val state by viewModel.model.state.collectAsState()

    AddBookScreenV2Content(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun AddBookScreenV2Content(
    state: AddBookState,
    onAction: (AddBookAction) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAction(AddBookAction.LoadBookCover(it)) }
    }

    val canSave = state.bookName.isNotBlank() && !state.isBookCoverLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BookechiBottomSheetScaffold(
            onDismissRequest = { onAction(AddBookAction.NavigateBack) },
            containerColor = FigmaLibraryBackground,
            header = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
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
                        onClick = { onAction(AddBookAction.SaveBook) },
                        enabled = canSave
                    ) {
                        if (state.isBookCoverLoading) {
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
                                onClick = { imagePickerLauncher.launch("image/*") }
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
                                imageUri = state.bookCoverPath?.toUri(),
                                onClick = { imagePickerLauncher.launch("image/*") }
                            )
                        }

                        if (state.bookCoverPath != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { onAction(AddBookAction.ClearBookCover) }
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
                            value = state.bookName,
                            onValueChange = { onAction(AddBookAction.UpdateBookName(it)) },
                            label = "Название книги",
                            placeholder = "Введите название"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        AddBookTextField(
                            value = state.bookAuthor,
                            onValueChange = { onAction(AddBookAction.UpdateBookAuthor(it)) },
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
                                    onAction(AddBookAction.UpdateReadingNow(!state.readingNow))
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
                                checked = state.readingNow,
                                onCheckedChange = { onAction(AddBookAction.UpdateReadingNow(it)) },
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
                            if (state.readingNow) {
                                AddBookTextField(
                                    value = state.bookCurrentPage.toTextFieldValue(),
                                    onValueChange = { input ->
                                        onAction(
                                            AddBookAction.UpdateCurrentPage(input.toIntOrZero())
                                        )
                                    },
                                    label = "Текущая",
                                    placeholder = "0",
                                    keyboardType = KeyboardType.Number,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            AddBookTextField(
                                value = state.bookPages.toTextFieldValue(),
                                onValueChange = { input ->
                                    onAction(
                                        AddBookAction.UpdateAllPages(input.toIntOrZero())
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

            if (state.bookCoverError != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = state.bookCoverError ?: "Ошибка сохранения",
                        color = FigmaSubtitle,
                        fontSize = 12.sp
                    )
                }
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

@Preview
@Composable
private fun AddBookScreenV2Preview() {
    AddBookScreenV2Content(
        state = AddBookState(
            bookName = "Хроники заводной птицы",
            bookAuthor = "Харуки Мураками",
            readingNow = true,
            bookPages = 1052,
            bookCurrentPage = 448
        ),
        onAction = {}
    )
}

@Preview
@Composable
private fun AddBookScreenV2EmptyPreview() {
    AddBookScreenV2Content(
        state = AddBookState(
            bookName = "",
            bookAuthor = "",
            readingNow = false,
            bookPages = 0,
            bookCurrentPage = 0
        ),
        onAction = {}
    )
}
