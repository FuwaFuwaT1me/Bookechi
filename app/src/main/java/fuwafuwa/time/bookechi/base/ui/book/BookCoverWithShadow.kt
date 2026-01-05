package fuwafuwa.time.bookechi.base.ui.book

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.ui.theme.BlueMain
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
                .padding(1.dp)
                .border(
                    width = 2.dp,
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
fun ProgressBookCoverShowcase(
    book: Book,
    imageUri: Uri?,
    progress: Float,
    modifier: Modifier = Modifier,
    circleSize: Dp = 200.dp,
    coverHeight: Dp = 140.dp,
    coverWidth: Dp = 100.dp,
    shadowOffset: Dp = 5.dp,
    onContainerClick: (() -> Unit)? = null,
    onCoverClick: (() -> Unit)? = null,
    onCoverLongTap: (() -> Unit)? = null,
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec = TweenSpec(
            durationMillis = 2250,
            delay = 300,
            easing = FastOutSlowInEasing
        ),
    )

    LaunchedEffect(true) {
        animationPlayed = true
    }

    val topPadding = 12.dp * (circleSize / 200.dp)

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = topPadding)
    ) {
        BookCoverShowcase(
            imageUri = imageUri,
            modifier = modifier,
            circleSize = circleSize,
            coverHeight = coverHeight,
            coverWidth = coverWidth,
            shadowOffset = shadowOffset,
            onContainerClick = onContainerClick,
            onCoverClick = onCoverClick,
            onCoverLongTap = onCoverLongTap,
        )

        Box {
            val startGapAngleDegrees = -90f + 22f
            val maxSweepAngle = 320f
            val arcStrokeWidth = 2.dp * (circleSize / 200.dp)

            Canvas(
                modifier = modifier
                    .size(circleSize)
                    .padding(arcStrokeWidth)
            ) {
                val strokeWidthPx = arcStrokeWidth.toPx()
                drawArc(
                    color = BlueMain,
                    startAngle = startGapAngleDegrees,
                    sweepAngle = maxSweepAngle * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }

            // Scaled sizes based on circleSize (default 200.dp as base)
            val scaleFactor = circleSize / 200.dp
            val badgeOffsetY = (-8).dp * scaleFactor
            val badgePaddingH = 8.dp * scaleFactor
            val badgePaddingV = 2.dp * scaleFactor
            val badgeCornerRadius = 16.dp * scaleFactor
            val mainFontSize = (14 * scaleFactor).sp
            val secondaryFontSize = (9 * scaleFactor).sp
            val buttonOuterSize = 16.dp * scaleFactor
            val buttonInnerSize = 14.dp * scaleFactor
            val buttonIconSize = 12.dp * scaleFactor
            val buttonOffsetX = 10.dp * scaleFactor

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = badgeOffsetY)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            color = BlueMain,
                            shape = RoundedCornerShape(badgeCornerRadius)
                        )
                        .padding(horizontal = badgePaddingH, vertical = badgePaddingV),
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontStyle = FontStyle.Italic,
                                fontSize = mainFontSize
                            )
                        ) {
                            append("${book.currentPage}")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = secondaryFontSize
                            )
                        ) {
                            append("/${book.pages}")
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = buttonOffsetX)
                        .size(buttonOuterSize)
                        .clip(CircleShape)
                        .background(color = Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(buttonInnerSize)
                            .clip(CircleShape)
                            .background(color = BlueMain)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(buttonIconSize),
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
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
private fun ProgressBookCoverShowcasePreview() {
    ProgressBookCoverShowcase(
        book = Book(
            id = 0,
            name = "asdf",
            author = "sadf",
            coverPath = null,
            currentPage = 256,
            pages = 1024
        ),
        imageUri = null,
        progress = 0.25f
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
