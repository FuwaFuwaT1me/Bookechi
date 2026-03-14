package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryAction
import fuwafuwa.time.bookechi.ui.feature.library.mvi.LibraryState
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle

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
        LibraryFilter.Planned -> state.books.filter { it.readingStatus == ReadingStatus.None }
        LibraryFilter.Favorite -> emptyList()
        LibraryFilter.Paused -> emptyList()
        LibraryFilter.Stopped -> emptyList()
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
                booksCount = state.books.size,
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
                        books = filteredBooks,
                        onBookClick = { book ->
                            onAction(LibraryAction.NavigateToBookDetails(book))
                        },
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                }
            }
        }

        LibraryAddBookFab(
            onClick = { onAction(LibraryAction.NavigateToAddBook) },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
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
