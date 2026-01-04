package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.ui.theme.BlackLight
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import fuwafuwa.time.bookechi.ui.theme.BlueMainDark

@Composable
fun EmptyBookList(
    onAddBookClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
        ,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier
                    .size(160.dp)
                ,
                painter = painterResource(R.drawable.empty_box),
                tint = BlueMainDark,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(16.dp)
                ,
                textAlign = TextAlign.Center,
                text = "Add your first book to track progress...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BlackLight,
            )
            Button(
                colors = ButtonColors(
                    containerColor = BlueMain,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                onClick = onAddBookClick
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                    ,
                    text = "Add book",
                    fontSize = 20.sp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PReviewEmptyBookList() {
    EmptyBookList(
        onAddBookClick = {}
    )
}
