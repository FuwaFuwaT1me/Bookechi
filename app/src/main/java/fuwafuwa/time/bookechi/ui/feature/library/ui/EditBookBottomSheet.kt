package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.NewBookCover
import fuwafuwa.time.bookechi.base.ui.util.BookechiBottomSheetScaffold
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryAction
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaLibraryBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.EditBookBottomSheet(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit,
    header: @Composable () -> Unit = { DefaultEditBookBottomSheetHeader() }
) {
    val book = state.editingBook ?: return

    var bookTitle by remember(book.id) { mutableStateOf(book.name) }
    var bookAuthor by remember(book.id) { mutableStateOf(book.author) }
    var readingStatus by remember(book.id) { mutableStateOf(book.readingStatus) }
    var isFavorite by remember(book.id) { mutableStateOf(book.isFavorite) }
    var coverPath by remember(book.id) { mutableStateOf(book.coverPath) }

    fun buildUpdatedBook(
        title: String = bookTitle,
        author: String = bookAuthor,
        status: ReadingStatus = readingStatus,
        favorite: Boolean = isFavorite,
        cover: String? = coverPath
    ): Book = book.copy(
        name = title,
        author = author,
        readingStatus = status,
        isFavorite = favorite,
        coverPath = cover
    )

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val newCoverPath = it.toString()
            coverPath = newCoverPath
            onAction(LibraryAction.UpdateBook(buildUpdatedBook(cover = newCoverPath)))
        }
    }

    BookechiBottomSheetScaffold(
        onDismissRequest = { onAction(LibraryAction.CancelEditingBook) },
        header = header,
        containerColor = FigmaLibraryBackground
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Обложка",
                                        color = FigmaTitle,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Нажмите, чтобы изменить",
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
                                    imageUri = coverPath?.toUri(),
                                    onClick = { coverPickerLauncher.launch("image/*") }
                                )
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
                            ModernTextField(
                                value = bookTitle,
                                onValueChange = {
                                    bookTitle = it
                                    onAction(LibraryAction.UpdateBook(buildUpdatedBook(title = it)))
                                },
                                label = "Название книги",
                                placeholder = "Введите название"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ModernTextField(
                                value = bookAuthor,
                                onValueChange = {
                                    bookAuthor = it
                                    onAction(LibraryAction.UpdateBook(buildUpdatedBook(author = it)))
                                },
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
                            Text(
                                text = "Статус чтения",
                                color = FigmaTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                ,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ReadingStatus.entries.forEach { status ->
                                    FilterChip(
                                        selected = readingStatus == status,
                                        onClick = {
                                            readingStatus = status
                                            onAction(
                                                LibraryAction.UpdateBook(
                                                    buildUpdatedBook(status = status)
                                                )
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = readingStatusLabel(status),
                                                fontSize = 12.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = FigmaTitle,
                                            selectedLabelColor = Color.White,
                                            containerColor = FigmaBackground,
                                            labelColor = FigmaTitle
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            FilledTonalButton(
                                onClick = {
                                    val newFavorite = !isFavorite
                                    isFavorite = newFavorite
                                    onAction(
                                        LibraryAction.UpdateBook(
                                            buildUpdatedBook(favorite = newFavorite)
                                        )
                                    )
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isFavorite) FigmaTitle else FigmaBackground,
                                    contentColor = if (isFavorite) Color.White else FigmaTitle
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isFavorite) "В избранном" else "В избранное",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
}

@Composable
private fun DefaultEditBookBottomSheetHeader() {
    Text(
        text = "Редактирование",
        color = FigmaTitle,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
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
            singleLine = true
        )
    }
}

private fun readingStatusLabel(status: ReadingStatus): String = when (status) {
    ReadingStatus.None -> "Не начата"
    ReadingStatus.Planned -> "В планах"
    ReadingStatus.Reading -> "Читаю"
    ReadingStatus.Paused -> "Пауза"
    ReadingStatus.Dropped -> "Брошена"
    ReadingStatus.Completed -> "Прочитана"
}
