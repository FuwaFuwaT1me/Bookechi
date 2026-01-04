package fuwafuwa.time.bookechi.base.ui.book

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.ui.theme.SuperLightGray

@Composable
fun BookCoverShowcase(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    circleSize: Dp = 200.dp,
    coverHeight: Dp = 140.dp,
    coverWidth: Dp = 100.dp,
    shadowOffset: Dp = 5.dp,
    onContainerClick: (() -> Unit)? = null,
    onCoverClick: (() -> Unit)? = null,
    onCoverLongTap: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.size(circleSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(color = SuperLightGray)
                .border(
                    width = 1.dp,
                    color = SuperLightGray,
                    shape = CircleShape
                )
                .border(
                    width = 3.dp,
                    color = Color.White,
                    shape = CircleShape
                )
                .then(
                    if (onContainerClick != null) {
                        Modifier.clickable { onContainerClick() }
                    } else {
                        Modifier
                    }
                )
        )

        BookCoverWithShadow(
            imageUri = imageUri,
            coverHeight = coverHeight,
            coverWidth = coverWidth,
            shadowOffset = shadowOffset,
            onClick = onCoverClick,
            onLongTap = onCoverLongTap,
        )
    }
}

@Composable
fun BookCoverWithShadow(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    coverHeight: Dp = 140.dp,
    coverWidth: Dp = 100.dp,
    shadowOffset: Dp = 5.dp,
    onClick: (() -> Unit)? = null,
    onLongTap: (() -> Unit)? = null,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .height(coverHeight)
                .width(coverWidth)
                .offset(x = shadowOffset, y = shadowOffset)
                .background(
                    color = Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
        )

        BookCover(
            modifier = Modifier
                .height(coverHeight)
                .width(coverWidth),
            imageUri = imageUri,
            onClick = onClick,
            onLongTap = onLongTap,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCoverShowcasePreview() {
    BookCoverShowcase(
        imageUri = "".toUri()
    )
}

@Preview(showBackground = true)
@Composable
private fun BookCoverShowcaseLargePreview() {
    BookCoverShowcase(
        imageUri = "".toUri(),
        circleSize = 240.dp,
        coverHeight = 170.dp,
        coverWidth = 120.dp,
        shadowOffset = 6.dp
    )
}

@Preview(showBackground = true)
@Composable
private fun BookCoverWithShadowPreview() {
    BookCoverWithShadow(
        imageUri = "".toUri(),
        coverHeight = 140.dp,
        coverWidth = 100.dp
    )
}
