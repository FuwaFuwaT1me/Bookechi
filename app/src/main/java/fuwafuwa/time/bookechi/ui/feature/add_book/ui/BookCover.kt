package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.ui.theme.BlueMainDark
import fuwafuwa.time.bookechi.ui.theme.SuperLightGray

@Composable
fun BookCover(
    state: AddBookState,
    onCoverChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
        ,
    ) {
        CoverPager(
            modifier = Modifier
            ,
            state = state,
            onCoverChange = onCoverChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Tabs()
    }
}

@Composable
private fun CoverPager(
    modifier: Modifier,
    state: AddBookState,
    onCoverChange: (String) -> Unit,
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf(state.bookCoverPath.toUri()) }
    val pagerState = PagerState(currentPage = 1) { 2 }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            onCoverChange(imageUri.toString())
        }
    }

    HorizontalPager(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
        ,
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> {

            }
            1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                ) {

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(200.dp)
                            .background(
                                color = SuperLightGray,
                                shape = CircleShape,
                            )
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
                        ,
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(140.dp)
                                    .width(100.dp)
                                    .offset(x = 5.dp, y = 5.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )

                            AsyncImage(
                                modifier = modifier
                                    .height(140.dp)
                                    .width(100.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        galleryLauncher.launch("image/*")
                                    }
                                    .drawWithContent {
                                        drawContent()
                                        drawLine(
                                            color = Color.Black.copy(alpha = 0.4f),
                                            start = Offset(10f, 0f),
                                            end = Offset(10f, size.height),
                                            strokeWidth = 4.dp.toPx()
                                        )
                                    }
                                ,

                                model = ImageRequest.Builder(context)
                                    .data(imageUri)
                                    .build(),
                                placeholder = painterResource(R.drawable.book_sample_cover),
                                error = painterResource(R.drawable.book_sample_cover),
                                contentDescription = "Выбранное изображение",
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(190.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.5f),
                                shape = CircleShape,
                            )
                        ,
                    ) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .drawBehind {
                                    drawCircle(
                                        color = Color.LightGray,
                                        radius = 10.dp.toPx(),
                                    )
                                }
                            ,
                            tint = Color.DarkGray,
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.Tabs() {

    val enabledTab = 0

    // TODO: чтобы одна кнопка была меньше другой и анимация перекатывания инейблинга при нажатии
    Row(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
        ,
    ) {
        Button(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(
                    height = if (enabledTab == 0) 32.dp else 24.dp,
                    width = if (enabledTab == 0) 64.dp else 56.dp
                )
            ,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonColors(
                containerColor = if (enabledTab == 0) BlueMainDark else Color.Gray,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            onClick = {

            }
        ) {
            Text(
                text = "Portrait",
                fontWeight = FontWeight.W400
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(
                    height = if (enabledTab == 1) 32.dp else 24.dp,
                    width = if (enabledTab == 1) 64.dp else 56.dp
                )
            ,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonColors(
                containerColor = if (enabledTab == 1) BlueMainDark else Color.Gray,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            onClick = {}
        ) {
            Text(
                text = "Custom",
                fontWeight = FontWeight.W400,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCoverPreview() {
    BookCover(
        state = AddBookState(
            bookName = "Хроники заводной птицы",
            bookAuthor = "Харуки Мураками",
            bookCoverPath = "",
            bookPages = 1052,
            bookCurrentPage = 448,
            isBookCoverLoading = false,
            bookCoverError = null
        ),
        onCoverChange = {}
    )
}
