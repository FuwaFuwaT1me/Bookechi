package fuwafuwa.time.bookechi.base.ui.book

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fuwafuwa.time.bookechi.R

@Composable
fun BookCover(
    imageUri: Uri,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // TODO: сделать сбоку странички как у реальной книжки
    // TODO: в зависимости от количества страниц делать книгу тоньше/толще?
    AsyncImage(
        modifier = modifier
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                onClick()
            }
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
            .background(Color.Red)
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

@Preview
@Composable
private fun PreviewBookCover() {
    BookCover(
        modifier = Modifier,
        imageUri = "".toUri(),
        onClick = {}
    )
}
