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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.EmptyState
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.ui.feature.library.mvi.AddingBookDraft
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryAction
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LocalBottomBarHeight
import kotlin.math.roundToInt

@Composable
fun LibraryContent(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit = {}
) {
    val colors = BookechiTheme.colors
    val horizontalPadding = Spacing.lg

    var activeFilter by remember { mutableStateOf(LibraryFilter.All) }
    val filteredBooks = state.books.filteredBy(activeFilter)

    // Коллапс хедера при скролле: полная высота хедера и текущее «сжатие» в px.
    var headerHeightPx by remember { mutableFloatStateOf(0f) }
    var headerCollapsePx by remember { mutableFloatStateOf(0f) }
    val collapseConnection = remember {
        object : NestedScrollConnection {
            // Скролл вверх — сперва сжимаем хедер, потом листается сетка.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta >= 0f || headerHeightPx <= 0f) return Offset.Zero
                val old = headerCollapsePx
                val new = (old - delta).coerceIn(0f, headerHeightPx)
                headerCollapsePx = new
                return Offset(0f, -(new - old))
            }

            // Скролл вниз — когда сетка уже вверху, разворачиваем хедер обратно.
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                if (delta <= 0f || headerHeightPx <= 0f) return Offset.Zero
                val old = headerCollapsePx
                val new = (old - delta).coerceIn(0f, headerHeightPx)
                headerCollapsePx = new
                return Offset(0f, -(new - old))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Spacing.xl)
                .nestedScroll(collapseConnection)
        ) {
            // Коллапсящийся хедер: при скролле вверх он уезжает вверх (а не тает),
            // оставляя только фильтры. В layout меряем полную высоту, уменьшаем
            // отдаваемую высоту на величину сжатия и сдвигаем контент вверх —
            // верхняя часть обрезается клипом.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val full = placeable.height
                            if (headerHeightPx != full.toFloat()) headerHeightPx = full.toFloat()
                            val collapse = headerCollapsePx.roundToInt().coerceIn(0, full)
                            layout(placeable.width, full - collapse) {
                                placeable.placeRelative(0, -collapse)
                            }
                        }
                ) {
                    LibraryHeader(
                        booksCount = state.books.size,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))
                }
            }

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
                            title = stringResource(R.string.lib_empty_title),
                            subtitle = stringResource(R.string.lib_empty_subtitle),
                            ctaText = stringResource(R.string.lib_add_book),
                            onCta = { onAction(LibraryAction.OpenAddBookSheet) },
                        )
                    }
                }

                filteredBooks.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.lib_empty_category),
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
                        onToggleFavorite = { book ->
                            onAction(LibraryAction.ToggleFavorite(book))
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
