package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.BookCover
import fuwafuwa.time.bookechi.base.ui.book.NewBookCover
import fuwafuwa.time.bookechi.base.ui.util.SimpleProgressIndicator
import fuwafuwa.time.bookechi.base.ui.util.optionalDetectTapGestures
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.ui.theme.BlueMainDark
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaBackgroundStroke
import fuwafuwa.time.bookechi.ui.theme.FigmaFire
import fuwafuwa.time.bookechi.ui.theme.FigmaGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaLightGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

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

@Composable
fun NewBookItem(
    book: Book,
    onBookClick: () -> Unit,
    onEditBookClick: () -> Unit,
    onDeleteBookClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    var imageUri by remember(book.id) { mutableStateOf(book.coverPath?.toUri()) }

    Card(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FigmaBackgroundStroke)
            .padding(1.dp)
            .optionalDetectTapGestures(
                onClick = onBookClick,
                onLongTap = {
                    showDropdownMenu = true
                }
            )
        ,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = FigmaBackground,
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
        ) {
            NewBookCover(
                modifier = Modifier,
                imageUri = imageUri,
                onClick = {},
                onLongTap = {
                    showDropdownMenu = true
                }
            )

            Spacer(
                modifier = Modifier.size(16.dp)
            )

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Заголовок",
                            color = FigmaTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "подзаголовок",
                            color = FigmaSubtitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                        IconButton(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Top),
                            shape = ShapeDefaults.ExtraSmall,
                            onClick = onEditBookClick,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Back",
                                tint = FigmaTitle
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.Bottom)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {

                            Text(
                                text = "${book.currentPage}",
                                color = FigmaTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.alignByBaseline()
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "/${book.pages}",
                                color = FigmaLightGrey.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.alignByBaseline()
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                modifier = Modifier
                                    .offset(0.dp, 8.dp)
                                ,
                                text = "${100 * book.currentPage / book.pages}%",
                                color = FigmaTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.size(12.dp))

                        SimpleProgressIndicator(
                            modifier = Modifier
                                .height(8.dp)
                                .fillMaxWidth(),
                            progress = 1f * book.currentPage / book.pages,
                            progressBarColor = FigmaGrey.copy(alpha = 0.8f),
                            trackColor = FigmaLightGrey,
                            cornerRadius = 8.dp,
                            innerProgressBarPadding = 3.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.Bottom)
                            ,
                            shape = RoundedCornerShape(8.dp),
                            colors = IconButtonColors(
                                containerColor = FigmaFire,
                                contentColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent
                            ),
                            onClick = onEditBookClick
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))
            }
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

@Preview(showBackground = true)
@Composable
private fun PreviewNewBookItem() {
    NewBookItem(
        Book(
            name = "Book 1",
            author = "Author 1",
            coverPath = "https://picsum.photos/200/300",
            currentPage = 54,
            pages = 120
        ),
        onBookClick = {},
        onEditBookClick = {},
        onDeleteBookClick = {}
    )
}
