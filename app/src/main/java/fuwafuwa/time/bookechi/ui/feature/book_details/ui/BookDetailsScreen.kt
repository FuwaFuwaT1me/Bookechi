package fuwafuwa.time.bookechi.ui.feature.book_details.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.ProgressBookCoverShowcase
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsAction
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsState
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsViewModel
import fuwafuwa.time.bookechi.ui.theme.BlackLight
import fuwafuwa.time.bookechi.ui.theme.BlueMain

@Composable
fun BookDetailsScreen(
    viewModel: BookDetailsViewModel
) {
    val state by viewModel.model.state.collectAsState()

    BookDetailsScreenPrivate(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun BookDetailsScreenPrivate(
    state: BookDetailsState,
    onAction: (BookDetailsAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Header(onBackClick = { onAction(BookDetailsAction.NavigateBack) })

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueMain)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Something went wrong",
                        color = Color.Red
                    )
                }
            }

            state.book != null -> {
                BookDetailsContent(book = state.book, onAction = onAction)
            }
        }
    }
}

@Composable
private fun Header(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = BlueMain
            )
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = "Book Details",
            fontSize = 24.sp,
            color = BlueMain,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Back",
                tint = BlueMain
            )
        }
    }
}

@Composable
private fun BookDetailsContent(
    book: Book,
    onAction: (BookDetailsAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressBookCoverShowcase(
            book = book,
            imageUri = book.coverPath?.toUri(),
            progress = 1f * book.currentPage / book.pages,
            circleSize = 360.dp,
            coverHeight = 240.dp,
            coverWidth = 168.dp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        BookInfoSection(book = book, onAction = onAction)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BookInfoSection(
    book: Book,
    onAction: (BookDetailsAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = book.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BlackLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = book.author,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        BookReadingActionButtons(
            readingStatus = book.readingStatus,
            onAction = onAction
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenPreview() {
    BookDetailsScreenPrivate(
        state = BookDetailsState(
            book = Book(
                id = 1,
                name = "Хроники заводной птицы",
                author = "Харуки Мураками",
                coverPath = null,
                pages = 1052,
                currentPage = 448,
                readingStatus = ReadingStatus.Reading
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenPausedPreview() {
    BookDetailsScreenPrivate(
        state = BookDetailsState(
            book = Book(
                id = 2,
                name = "1984",
                author = "George Orwell",
                coverPath = "https://example.com/cover.jpg",
                pages = 328,
                currentPage = 150,
                readingStatus = ReadingStatus.Paused
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenNotStartedPreview() {
    BookDetailsScreenPrivate(
        state = BookDetailsState(
            book = Book(
                id = 3,
                name = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                coverPath = null,
                pages = 180,
                currentPage = 0,
                readingStatus = ReadingStatus.None
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenFinishedPreview() {
    BookDetailsScreenPrivate(
        state = BookDetailsState(
            book = Book(
                id = 4,
                name = "Война и мир",
                author = "Лев Толстой",
                coverPath = null,
                pages = 1225,
                currentPage = 1225,
                readingStatus = ReadingStatus.Stopped
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenLoadingPreview() {
    BookDetailsScreenPrivate(
        state = BookDetailsState(
            book = null,
            isLoading = true
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenErrorPreview() {
    BookDetailsScreenPrivate(
        state = BookDetailsState(
            book = null,
            error = "Failed to load book"
        ),
        onAction = {}
    )
}
