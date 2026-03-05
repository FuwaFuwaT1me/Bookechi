package fuwafuwa.time.bookechi.base.ui.book

import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.util.optionalDetectTapGestures
import fuwafuwa.time.bookechi.ui.theme.FigmaBookCover

@Composable
fun BookCover(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongTap: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    // TODO: сделать сбоку странички как у реальной книжки
    // TODO: в зависимости от количества страниц делать книгу тоньше/толще?
    AsyncImage(
        modifier = modifier
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(4.dp))
            .optionalDetectTapGestures(onClick, onLongTap)
            .drawWithContent {
                val startOffsetX = size.width * 0.015f
                val strokeWidth = startOffsetX.dp.toPx()

                drawContent()
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(startOffsetX, 0f),
                    end = Offset(startOffsetX, size.height),
                    strokeWidth = strokeWidth
                )
            }
        ,
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .build(),
        placeholder = painterResource(R.drawable.book_sample_cover),
        error = painterResource(R.drawable.book_sample_cover),
        contentDescription = "Выбранное изображение",
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
fun NewBookCover(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongTap: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    AsyncImage(
        modifier = modifier
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(8.dp))
            .optionalDetectTapGestures(onClick, onLongTap)
        ,
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .build(),
        placeholder = ColorPainter(FigmaBookCover),
        error = ColorPainter(FigmaBookCover),
        contentDescription = "Выбранное изображение",
        contentScale = ContentScale.FillBounds,
    )
}

@Preview
@Composable
private fun PreviewBookCover() {
    BookCover(
        modifier = Modifier,
        imageUri = "".toUri(),
        onClick = {}
    )
}
