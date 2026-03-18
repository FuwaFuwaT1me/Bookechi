package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.NewBookCover
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaBackgroundStroke
import fuwafuwa.time.bookechi.ui.theme.FigmaBookCover
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
fun LibraryBookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(FigmaBackground)
            .border(
                width = 1.dp,
                color = FigmaBackgroundStroke,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(FigmaBookCover)
            ) {
                NewBookCover(
                    imageUri = book.coverPath?.toUri(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                )
            }

            val tag = bookTag(book)
            if (tag != null) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF0DCC6))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tag,
                        color = FigmaTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (book.readingStatus == ReadingStatus.Completed) {
                Box(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.TopEnd)
                        .padding(8.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = FigmaTitle,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.name,
                    color = FigmaTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = book.author,
                    color = FigmaSubtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = FigmaTitle
            )
        }
    }
}

private fun bookTag(book: Book): String? = when (book.readingStatus) {
    ReadingStatus.None -> null
    ReadingStatus.Planned -> "В планах"
    ReadingStatus.Reading -> "Читаю"
    ReadingStatus.Paused -> "Приостановлена"
    ReadingStatus.Dropped -> "Брошена"
    ReadingStatus.Completed -> "Прочитана"
}

@Preview
@Composable
private fun LibraryBookCardPreview() {
    LibraryBookCard(
        book = LibraryPreviewData.books().first(),
        onClick = {}
    )
}
