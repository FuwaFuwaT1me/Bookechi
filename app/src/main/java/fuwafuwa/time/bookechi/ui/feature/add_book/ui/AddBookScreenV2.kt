package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.NewBookCover
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.FilterChip
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.WarmTextField
import fuwafuwa.time.bookechi.base.ui.util.BookechiBottomSheetScaffold
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookAction
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookMode
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Локальная мок-модель результата поиска книги. */
// TODO: replace with Open Library API
private data class BookSearchResult(
    val title: String,
    val author: String,
    val pages: Int,
    val hasCover: Boolean = false,
)

// TODO: replace with Open Library API
private val MockSearchResults = listOf(
    BookSearchResult("Норвежский лес", "Харуки Мураками", 384, hasCover = true),
    BookSearchResult("Хроники заводной птицы", "Харуки Мураками", 1052),
    BookSearchResult("Думай медленно… решай быстро", "Даниэль Канеман", 656, hasCover = true),
    BookSearchResult("1984", "Джордж Оруэлл", 328),
    BookSearchResult("Маленькая жизнь", "Ханья Янагихара", 720, hasCover = true),
    BookSearchResult("Дом, в котором…", "Мариам Петросян", 960),
)

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
    val colors = BookechiTheme.colors

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAction(AddBookAction.LoadBookCover(it)) }
    }

    val isRequiredInputValid = state.bookName.trim().isNotEmpty() && state.bookPages > 0
    val bookNameHasError = state.showValidationErrors && state.bookName.trim().isEmpty()
    val pagesHasError = state.showValidationErrors && state.bookPages <= 0
    val isSearch = state.mode == AddBookMode.Search

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BookechiBottomSheetScaffold(
            onDismissRequest = { onAction(AddBookAction.NavigateBack) },
            containerColor = colors.canvas,
            header = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSearch) "Найти книгу" else "Добавить книгу",
                            color = colors.textPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f)
                        )

                        if (!isSearch) {
                            TextButton(
                                onClick = { onAction(AddBookAction.SaveBook) },
                                enabled = !state.isBookCoverLoading
                            ) {
                                if (state.isBookCoverLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = colors.accent,
                                    )
                                } else {
                                    Text(
                                        text = "Сохранить",
                                        color = if (isRequiredInputValid) {
                                            colors.accent
                                        } else {
                                            colors.textSecondary
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }
                            }
                        }
                    }

                    if (!isSearch && state.showValidationErrors && !isRequiredInputValid) {
                        Text(
                            text = "Заполните обязательные поля",
                            color = colors.fire,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(horizontal = Spacing.md, vertical = 2.dp)
                        )
                    }
                }
            }
        ) {
            if (isSearch) {
                searchStep(
                    state = state,
                    onAction = onAction,
                )
            } else {
                formStep(
                    state = state,
                    colors = colors,
                    bookNameHasError = bookNameHasError,
                    pagesHasError = pagesHasError,
                    onAction = onAction,
                    onPickCover = { imagePickerLauncher.launch("image/*") },
                )
            }
        }
    }
}

// ============================================================================
// Шаг 1. Поиск книги (мок-данные)
// ============================================================================

private fun androidx.compose.foundation.lazy.LazyListScope.searchStep(
    state: AddBookState,
    onAction: (AddBookAction) -> Unit,
) {
    item {
        WarmTextField(
            value = state.searchQuery,
            onValueChange = { onAction(AddBookAction.UpdateSearchQuery(it)) },
            label = "Поиск",
            placeholder = "Найти по названию или автору",
        )
    }

    item { Spacer(modifier = Modifier.height(Spacing.lg)) }

    item { SectionLabel(text = "Результаты") }

    item { Spacer(modifier = Modifier.height(Spacing.sm)) }

    val query = state.searchQuery.trim()
    val results = if (query.isEmpty()) {
        MockSearchResults
    } else {
        MockSearchResults.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.author.contains(query, ignoreCase = true)
        }
    }

    items(results.size) { index ->
        SearchResultRow(
            result = results[index],
            onClick = {
                val r = results[index]
                onAction(
                    AddBookAction.SelectSearchResult(
                        title = r.title,
                        author = r.author,
                        pages = r.pages,
                        hasCover = r.hasCover,
                    )
                )
            },
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
    }

    item { Spacer(modifier = Modifier.height(Spacing.md)) }

    item {
        TextButton(
            onClick = { onAction(AddBookAction.EnterManually) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Ввести вручную",
                color = BookechiTheme.colors.accent,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun SearchResultRow(
    result: BookSearchResult,
    onClick: () -> Unit,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surface)
            .border(width = 1.dp, color = colors.stroke, shape = DsShapes.card)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BookCover(
            coverPath = null,
            title = result.title,
            author = result.author,
            width = 44.dp,
        )

        Spacer(modifier = Modifier.width(Spacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${result.author} · ${result.pages} стр.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }

        Spacer(modifier = Modifier.width(Spacing.sm))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.accent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Добавить",
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ============================================================================
// Шаг 2. Форма добавления (предзаполненная из поиска или пустая)
// ============================================================================

private fun androidx.compose.foundation.lazy.LazyListScope.formStep(
    state: AddBookState,
    colors: fuwafuwa.time.bookechi.ui.theme.BookechiColors,
    bookNameHasError: Boolean,
    pagesHasError: Boolean,
    onAction: (AddBookAction) -> Unit,
    onPickCover: () -> Unit,
) {
    // Блок «Обложка найдена» / выбор обложки
    item {
        WarmCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.coverFromSearch && state.bookCoverPath == null) {
                            "Обложка найдена"
                        } else {
                            "Обложка"
                        },
                        color = colors.textPrimary,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = "Нажмите, чтобы выбрать",
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                TextButton(onClick = onPickCover) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Сменить",
                        color = colors.accent,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (state.bookCoverPath == null && state.coverFromSearch) {
                    BookCover(
                        coverPath = null,
                        title = state.bookName.ifEmpty { "Обложка" },
                        author = state.bookAuthor,
                        width = 120.dp,
                    )
                } else {
                    NewBookCover(
                        modifier = Modifier
                            .height(170.dp)
                            .width(120.dp),
                        imageUri = state.bookCoverPath?.toUri(),
                        onClick = onPickCover
                    )
                }
            }

            if (state.bookCoverPath != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onAction(AddBookAction.ClearBookCover) }) {
                        Text(
                            text = "Удалить обложку",
                            color = colors.textSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }

    item { Spacer(modifier = Modifier.height(Spacing.lg)) }

    // Название + автор
    item {
        WarmCard {
            WarmTextField(
                value = state.bookName,
                onValueChange = { onAction(AddBookAction.UpdateBookName(it)) },
                label = "Название книги",
                placeholder = "Введите название",
            )
            if (bookNameHasError) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Введите название книги",
                    color = colors.fire,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            WarmTextField(
                value = state.bookAuthor,
                onValueChange = { onAction(AddBookAction.UpdateBookAuthor(it)) },
                label = "Автор",
                placeholder = "Введите автора",
            )
        }
    }

    item { Spacer(modifier = Modifier.height(Spacing.lg)) }

    // Статус чтения + страницы
    item {
        WarmCard {
            SectionLabel(text = "Статус")
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                FilterChip(
                    text = "Читаю",
                    selected = state.readingNow,
                    onClick = { onAction(AddBookAction.UpdateReadingNow(true)) },
                )
                FilterChip(
                    text = "В планах",
                    selected = !state.readingNow,
                    onClick = { onAction(AddBookAction.UpdateReadingNow(false)) },
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                if (state.readingNow) {
                    WarmTextField(
                        value = state.bookCurrentPage.toTextFieldValue(),
                        onValueChange = { input ->
                            onAction(AddBookAction.UpdateCurrentPage(input.toIntOrZero()))
                        },
                        label = "Текущая",
                        placeholder = "",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }

                WarmTextField(
                    value = state.bookPages.toTextFieldValue(),
                    onValueChange = { input ->
                        onAction(AddBookAction.UpdateAllPages(input.toIntOrZero()))
                    },
                    label = "Всего страниц",
                    placeholder = "",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
            if (pagesHasError) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Укажите количество страниц",
                    color = colors.fire,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    if (state.bookCoverError != null) {
        item { Spacer(modifier = Modifier.height(Spacing.lg)) }
        item {
            Text(
                text = state.bookCoverError ?: "Ошибка сохранения",
                color = colors.fire,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    item { Spacer(modifier = Modifier.height(Spacing.lg)) }

    item {
        PrimaryButton(
            text = "Назад к поиску",
            onClick = { onAction(AddBookAction.BackToSearch) },
        )
    }
}

@Composable
private fun WarmCard(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DsShapes.card)
            .background(colors.surface)
            .border(width = 1.dp, color = colors.stroke, shape = DsShapes.card)
            .padding(Spacing.lg),
        content = content,
    )
}

private fun Int.toTextFieldValue(): String = if (this > 0) this.toString() else ""

private fun String.toIntOrZero(): Int = toIntOrNull()?.coerceAtLeast(0) ?: 0

// ============================================================================
// Previews
// ============================================================================

@Composable
private fun SearchStepPreviewContent(state: AddBookState) {
    Surface(color = BookechiTheme.colors.canvas) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        ) {
            item {
                Text(
                    text = "Найти книгу",
                    color = BookechiTheme.colors.textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
            searchStep(state = state, onAction = {})
        }
    }
}

@Composable
private fun FormStepPreviewContent(state: AddBookState) {
    val colors = BookechiTheme.colors
    Surface(color = colors.canvas) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        ) {
            item {
                Text(
                    text = "Добавить книгу",
                    color = colors.textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
            formStep(
                state = state,
                colors = colors,
                bookNameHasError = false,
                pagesHasError = false,
                onAction = {},
                onPickCover = {},
            )
        }
    }
}

@Preview(name = "Search Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun SearchStepPreviewLight() {
    BookechiTheme(darkTheme = false) {
        SearchStepPreviewContent(
            AddBookState(
                bookName = "", bookAuthor = "", readingNow = false,
                bookPages = 0, bookCurrentPage = 0,
                mode = AddBookMode.Search, searchQuery = "",
            )
        )
    }
}

@Preview(name = "Search Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun SearchStepPreviewDark() {
    BookechiTheme(darkTheme = true) {
        SearchStepPreviewContent(
            AddBookState(
                bookName = "", bookAuthor = "", readingNow = false,
                bookPages = 0, bookCurrentPage = 0,
                mode = AddBookMode.Search, searchQuery = "мур",
            )
        )
    }
}

@Preview(name = "Prefilled Form Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun FormStepPreviewLight() {
    BookechiTheme(darkTheme = false) {
        FormStepPreviewContent(
            AddBookState(
                bookName = "Норвежский лес",
                bookAuthor = "Харуки Мураками",
                readingNow = true,
                bookPages = 384,
                bookCurrentPage = 120,
                mode = AddBookMode.Form,
                coverFromSearch = true,
            )
        )
    }
}

@Preview(name = "Prefilled Form Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun FormStepPreviewDark() {
    BookechiTheme(darkTheme = true) {
        FormStepPreviewContent(
            AddBookState(
                bookName = "Думай медленно… решай быстро",
                bookAuthor = "Даниэль Канеман",
                readingNow = false,
                bookPages = 656,
                bookCurrentPage = 0,
                mode = AddBookMode.Form,
                coverFromSearch = true,
            )
        )
    }
}
