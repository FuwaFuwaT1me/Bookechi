package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookAction
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import fuwafuwa.time.bookechi.ui.theme.SuperLightGray
import kotlinx.serialization.Serializable

@Serializable
data object AddBookScreen : Screen

@Composable
fun AddBookScreen(
    viewModel: AddBookViewModel
) {
    val state by viewModel.model.state.collectAsState()

    AddBookScreenPrivate(
        state = state,
        onSaveBookAction = { name, author, coverPath ->
            viewModel.sendAction(
                AddBookAction.SaveBook(
                    bookName = name,
                    bookAuthor = author,
                    bookCoverPath = coverPath
                )
            )
        }
    )
}

@Composable
private fun AddBookScreenPrivate(
    state: AddBookState,
    onSaveBookAction: (String, String, String) -> Unit
) {
    val bookCoverPath = remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Header()

            BookCover(
                modifier = Modifier
                ,
                state = state,
                onCoverChange = { cover ->
                    bookCoverPath.value = cover
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFields(state)
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
                .align(Alignment.BottomCenter),
            colors = ButtonColors(
                containerColor = BlueMain,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                onSaveBookAction(
//                    bookNameState.text.toString(),
//                    bookAuthorState.text.toString(),
                    "book name",
                    "boko author",
                    bookCoverPath.value
                )
            },
        ) {
            Text("Save book")
        }
    }
}

@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ,
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
            ,
            textAlign = TextAlign.Center,
            text = "Add book",
            fontSize = 24.sp,
            color = BlueMain,
            fontWeight = FontWeight.Bold
        )

        Button(
            modifier = Modifier
                .size(width = 52.dp, height = 32.dp)
                .align(Alignment.CenterEnd)
            ,
            colors = ButtonColors(
                containerColor = BlueMain,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp),
            onClick = {

            }
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                ,
                imageVector = Icons.Outlined.Check,
                tint = Color.White,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun InputFields(state: AddBookState) {
    val bookNameState = rememberTextFieldState("")
    val bookAuthorState = rememberTextFieldState("")

    var bookNameFocusState by remember { mutableStateOf(false) }
    var bookAuthorFocusState by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SuperLightGray)
            .padding(start = 8.dp, end = 8.dp, top = 16.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.size(32.dp))

            AddBookTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                state = bookNameState,
                hint = "Name"
            )

            Spacer(modifier = Modifier.size(8.dp))

            AddBookTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                state = bookAuthorState,
                hint = "Author",
            )

            Spacer(modifier = Modifier.size(24.dp))

            Pages()
        }
    }
}

@Composable
private fun Pages() {
    val currentPageState = rememberTextFieldState("")
    val allPagesState = rememberTextFieldState("")

    Row {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 4.dp),
                color = Color.Gray,
                text = "Current page"
            )

            Spacer(modifier = Modifier.height(4.dp))

            AddBookTextField(
                modifier = Modifier
                    .height(38.dp),
                state = currentPageState,
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Bottom)
                .padding(bottom = 8.dp)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "/",
                color = BlueMain,
                fontSize = 24.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 4.dp),
                color = Color.Gray,
                text = "All pages"
            )

            Spacer(modifier = Modifier.height(4.dp))

            AddBookTextField(
                modifier = Modifier
                    .height(38.dp),
                state = allPagesState,
            )
        }

        // TODO: снизу \/ вот так чтобы красиво проценты считались по анимации
    }
}

@Preview(showBackground = true)
@Composable
private fun AddBookScreenPreview() {
    AddBookScreenPrivate(
        state = AddBookState(
            bookName = "Хроники заводной птицы",
            bookAuthor = "Харуки Мураками",
            bookCoverPath = "",
            bookPages = 1052,
            bookCurrentPage = 448,
            isBookCoverLoading = false,
            bookCoverError = null
        ),
        onSaveBookAction = { name, author, cover ->

        }
    )
}
