package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fuwafuwa.time.bookechi.base.ui.ds.EmptyState
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.ui.feature.library.mvi.AddingBookDraft
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryAction
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LocalBottomBarHeight

@Composable
fun LibraryContent(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit = {}
) {
    val colors = BookechiTheme.colors
    val horizontalPadding = Spacing.lg

    var activeFilter by remember { mutableStateOf(LibraryFilter.All) }
    val filteredBooks = state.books.filteredBy(activeFilter)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Spacing.xl)
        ) {
            LibraryHeader(
                booksCount = state.books.size,
                modifier = Modifier.padding(horizontal = horizontalPadding)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            LibraryFiltersRow(
                activeFilter = activeFilter,
                onFilterChange = { activeFilter = it },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xxl),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = colors.accent)
                    }
                }

                state.error != null -> {
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                }

                state.books.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyState(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            title = "Библиотека пуста",
                            subtitle = "Добавьте книгу — бумажную, электронную, какую угодно.",
                            ctaText = "Добавить книгу",
                            onCta = { onAction(LibraryAction.OpenAddBookSheet) },
                        )
                    }
                }

                filteredBooks.isEmpty() -> {
                    Text(
                        text = "Книг в этой категории пока нет.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
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
                        contentPadding = PaddingValues(
                            bottom = LocalBottomBarHeight.current + Spacing.lg,
                        ),
                    )
                }
            }
        }

        if (state.books.isNotEmpty()) {
            LibraryAddBookFab(
                onClick = { onAction(LibraryAction.OpenAddBookSheet) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = LocalBottomBarHeight.current + Spacing.md)
            )
        }

        LibraryAddBookBottomSheet(
            state = state,
            onAction = onAction
        )

        EditBookBottomSheet(
            state = state,
            onAction = onAction
        )
    }
}

@Preview(name = "Library Filled Light")
@Composable
private fun LibraryContentFilledLight() {
    BookechiTheme(darkTheme = false) {
        Surface {
            LibraryContent(
                state = LibraryState(books = LibraryPreviewData.books(), isLoading = false)
            )
        }
    }
}

@Preview(name = "Library Filled Dark")
@Composable
private fun LibraryContentFilledDark() {
    BookechiTheme(darkTheme = true) {
        Surface {
            LibraryContent(
                state = LibraryState(books = LibraryPreviewData.books(), isLoading = false)
            )
        }
    }
}

@Preview(name = "Library Empty Light")
@Composable
private fun LibraryContentEmptyLight() {
    BookechiTheme(darkTheme = false) {
        Surface {
            LibraryContent(state = LibraryState(books = emptyList(), isLoading = false))
        }
    }
}

@Preview(name = "Library Empty Dark")
@Composable
private fun LibraryContentEmptyDark() {
    BookechiTheme(darkTheme = true) {
        Surface {
            LibraryContent(state = LibraryState(books = emptyList(), isLoading = false))
        }
    }
}

@Preview(name = "Add Sheet Light")
@Composable
private fun LibraryAddSheetLight() {
    BookechiTheme(darkTheme = false) {
        Surface {
            LibraryContent(
                state = LibraryState(
                    books = LibraryPreviewData.books(),
                    addingBook = AddingBookDraft(name = "Норвежский лес", author = "Харуки Мураками", pages = "296"),
                )
            )
        }
    }
}

@Preview(name = "Add Sheet Dark")
@Composable
private fun LibraryAddSheetDark() {
    BookechiTheme(darkTheme = true) {
        Surface {
            LibraryContent(
                state = LibraryState(
                    books = LibraryPreviewData.books(),
                    addingBook = AddingBookDraft(name = "Норвежский лес", author = "Харуки Мураками", pages = "296"),
                )
            )
        }
    }
}

@Preview(name = "Edit Sheet Light")
@Composable
private fun LibraryEditSheetLight() {
    BookechiTheme(darkTheme = false) {
        Surface {
            LibraryContent(
                state = LibraryState(
                    books = LibraryPreviewData.books(),
                    editingBook = LibraryPreviewData.books().first().copy(isFavorite = true),
                )
            )
        }
    }
}

@Preview(name = "Edit Sheet Dark")
@Composable
private fun LibraryEditSheetDark() {
    BookechiTheme(darkTheme = true) {
        Surface {
            LibraryContent(
                state = LibraryState(
                    books = LibraryPreviewData.books(),
                    editingBook = LibraryPreviewData.books().first().copy(isFavorite = true),
                )
            )
        }
    }
}
