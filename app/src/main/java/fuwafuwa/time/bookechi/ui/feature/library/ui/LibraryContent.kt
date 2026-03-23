@file:OptIn(ExperimentalMaterial3Api::class)

package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.NewBookCover
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryAction
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaLibraryBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
import kotlinx.coroutines.launch

@Composable
fun LibraryContent(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit = {}
) {
    val horizontalPadding = 16.dp
    var activeFilter by remember { mutableStateOf(LibraryFilter.All) }
    val filteredBooks = when (activeFilter) {
        LibraryFilter.All -> state.books
        LibraryFilter.Reading -> state.books.filter { it.readingStatus == ReadingStatus.Reading }
        LibraryFilter.Completed -> state.books.filter { it.readingStatus == ReadingStatus.Completed }
        LibraryFilter.Planned -> state.books.filter { it.readingStatus == ReadingStatus.Planned }
        LibraryFilter.Favorite -> state.books.filter { it.isFavorite}
        LibraryFilter.Paused -> state.books.filter { it.readingStatus == ReadingStatus.Paused }
        LibraryFilter.Dropped -> state.books.filter { it.readingStatus == ReadingStatus.Dropped }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
        ) {
            LibraryHeader(
                booksCount = filteredBooks.size,
                onEditClick = {},
                modifier = Modifier.padding(horizontal = horizontalPadding)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LibraryFiltersRow(
                activeFilter = activeFilter,
                onFilterChange = { activeFilter = it },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding, vertical = 24.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Что-то пошло не так",
                        color = FigmaSubtitle,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                }

                filteredBooks.isEmpty() -> {
                    Text(
                        text = "Книг в этой категории пока нет.",
                        color = FigmaSubtitle,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                }

                else -> {
                    LibraryBooksGrid(
                        modifier = Modifier.padding(horizontal = horizontalPadding),
                        books = filteredBooks,
                        onBookClick = { book ->
                            onAction(LibraryAction.NavigateToBookDetails(book))
                        },
                        onEditClick = { book ->
                            onAction(LibraryAction.EditBook(book))
                        }
                    )
                }
            }
        }

        LibraryAddBookFab(
            onClick = { onAction(LibraryAction.NavigateToAddBook) },
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        EditBookBottomSheet(state, onAction)
    }
}

@Composable
private fun BoxScope.EditBookBottomSheet(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit
) {
    val book = state.editingBook ?: return

    var bookTitle by remember(book.id) { mutableStateOf(book.name) }
    var bookAuthor by remember(book.id) { mutableStateOf(book.author) }
    var readingStatus by remember(book.id) { mutableStateOf(book.readingStatus) }
    var isFavorite by remember(book.id) { mutableStateOf(book.isFavorite) }
    var coverPath by remember(book.id) { mutableStateOf(book.coverPath) }

    val blockParentSheetScroll = rememberBlockParentSheetScroll()
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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

    suspend fun dismissSheetInternal() {
        sheetState.hide()
        onAction(LibraryAction.CancelEditingBook)
    }

    fun dismissSheet() {
        scope.launch {
            dismissSheetInternal()
        }
    }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val newCoverPath = it.toString()
            coverPath = newCoverPath
            onAction(LibraryAction.UpdateBook(buildUpdatedBook(cover = newCoverPath)))
        }
    }

    val screenHeightPx = LocalWindowInfo.current.containerSize.height
        .toFloat()
        .coerceAtLeast(1f)

    val scrimAlpha by remember(sheetState, screenHeightPx) {
        derivedStateOf {
            val offsetPx = runCatching { sheetState.requireOffset() }
                .getOrDefault(screenHeightPx)

            val progress = (1f - (offsetPx / screenHeightPx))
                .coerceIn(0f, 1f)

            0.5f * progress
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (scrimAlpha > 0.05f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { dismissSheet() })
                    }
            )
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { dismissSheet() },
            containerColor = FigmaLibraryBackground,
            scrimColor = Color.Transparent,
            dragHandle = {
                BottomSheetDefaults.DragHandle()
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .nestedScroll(blockParentSheetScroll),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 24.dp
                )
            ) {
                item {
                    Text(
                        text = "Редактирование",
                        color = FigmaTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                ReadingStatus.values().forEachIndexed { index, status ->
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

                                    if (index != ReadingStatus.values().lastIndex) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
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
    }
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

@Composable
private fun rememberBlockParentSheetScroll(): NestedScrollConnection {
    return remember {
        object : NestedScrollConnection {

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Съедаем весь оставшийся вертикальный скролл,
                // который внутренний контент уже не смог обработать.
                return Offset(x = 0f, y = available.y)
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                // Съедаем остаточный fling, чтобы он не ушел в sheet.
                return Velocity(x = 0f, y = available.y)
            }
        }
    }
}

@Preview
@Composable
private fun LibraryContentPreview() {
    LibraryContent(
        state = LibraryState(
            books = LibraryPreviewData.books(),
            isLoading = false
        )
    )
}

@Preview
@Composable
private fun EditingBookPreview() {
    LibraryContent(
        state = LibraryState(
            books = LibraryPreviewData.books(),
            isLoading = false,
            editingBook = Book(
                name = "name",
                author = "author",
                coverPath = null,
                pages = 256,
                currentPage = 54,
                isFavorite = false,
            )
        )
    )
}
