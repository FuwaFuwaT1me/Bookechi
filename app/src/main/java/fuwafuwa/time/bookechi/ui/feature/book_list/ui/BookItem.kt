package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.BookCover
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.ui.theme.BlueMainDark

@Composable
fun BookItem(
    book: Book,
    onClick: () -> Unit,
    onDeleteBookClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    var imageUri by remember(book.id) { mutableStateOf(book.coverPath?.toUri()) }

    Box(
        modifier = modifier
            .wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .height(100.dp)
                .width(70.dp)
        ) {
            BookCover(
                modifier = Modifier,
                imageUri = imageUri,
                onClick = onClick,
                onLongTap = {
                    showDropdownMenu = true
                },
            )

            if (book.currentPage != 0 && book.pages != 0 && book.currentPage < book.pages) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .height(8.dp)
                        .background(Color.White)
                        .align(Alignment.BottomCenter)
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 2.dp),
                        progress = { 1f * book.currentPage / book.pages },
                        color = BlueMainDark,
                        trackColor = Color.White,
                        strokeCap = StrokeCap.Square,
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )
                }
            }

            // TODO: название/описание, которое можно раскрыть вниз под рядом книг
        }

        BookItemDropdownMenu(
            showMenu = showDropdownMenu,
            onDismissRequest = {
                showDropdownMenu = false
            },
            onDeleteBookClick = {
                showDropdownMenu = false
                onDeleteBookClick()
            }
        )
    }
}

@Preview
@Composable
private fun PreviewBookItem() {
    BookItem(
        Book(
            name = "Book 1",
            author = "Author 1",
            coverPath = "https://picsum.photos/200/300",
            currentPage = 25,
            pages = 100
        ),
        onClick = {},
        onDeleteBookClick = {}
    )
}

@Preview
@Composable
private fun PreviewZeroProgressBookItem() {
    BookItem(
        Book(
            name = "Book 1",
            author = "Author 1",
            coverPath = "https://picsum.photos/200/300",
            currentPage = 0,
            pages = 100
        ),
        onClick = {},
        onDeleteBookClick = {}
    )
}
