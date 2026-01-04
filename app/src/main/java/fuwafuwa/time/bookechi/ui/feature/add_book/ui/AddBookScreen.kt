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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
        onAction = viewModel::sendAction
    )
}

@Composable
private fun AddBookScreenPrivate(
    state: AddBookState,
    onAction: (AddBookAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Header(
                state = state,
                onAction = onAction
            )

            BookPager(
                modifier = Modifier
                ,
                state = state,
                onAddBookCover = { uri -> onAction(AddBookAction.LoadBookCover(uri)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFields(
                state = state,
                onAction = onAction
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(46.dp)
                .align(Alignment.BottomCenter)
            ,
            colors = ButtonColors(
                containerColor = BlueMain,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            onClick = { onAction(AddBookAction.SaveBook) },
        ) {
            Text("Save book")
        }
    }
}

@Composable
private fun Header(
    state: AddBookState,
    onAction: (AddBookAction) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ,
    ) {
        Button(
            modifier = Modifier
                .size(width = 52.dp, height = 32.dp)
                .align(Alignment.CenterStart)
            ,
            colors = ButtonColors(
                containerColor = BlueMain,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp),
            onClick = { onAction(AddBookAction.NavigateBack) }
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = Color.White,
                contentDescription = null
            )
        }

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

        if (state.bookCoverPath != null) {
            AddBookDropdownMenu(
                showMenu = showMenu,
                onShowMenuChange = { showMenuChange ->
                    showMenu = showMenuChange
                },
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun InputFields(
    state: AddBookState,
    onAction: (AddBookAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SuperLightGray)
            .padding(start = 8.dp, end = 8.dp, top = 16.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.size(16.dp))

            AddBookTextField(
                modifier = Modifier.fillMaxWidth(),
                initialValue = state.bookName,
                hint = "Name",
                onValueChange = { onAction(AddBookAction.UpdateBookName(it)) }
            )

            Spacer(modifier = Modifier.size(8.dp))

            AddBookTextField(
                modifier = Modifier.fillMaxWidth(),
                initialValue = state.bookAuthor,
                hint = "Author",
                onValueChange = { onAction(AddBookAction.UpdateBookAuthor(it)) }
            )

            Spacer(modifier = Modifier.size(24.dp))

            Pages(
                state = state,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun Pages(
    state: AddBookState,
    onAction: (AddBookAction) -> Unit,
) {
    Row {
        PageField(
            modifier = Modifier.weight(1f),
            label = "Current page",
            onValueChange = { it.toIntOrNull()?.let { page -> onAction(AddBookAction.UpdateCurrentPage(page)) } }
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Bottom)
                .padding(bottom = 8.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "/",
                color = BlueMain,
                fontSize = 24.sp
            )
        }

        PageField(
            modifier = Modifier.weight(1f),
            label = "All pages",
            onValueChange = { it.toIntOrNull()?.let { pages -> onAction(AddBookAction.UpdateAllPages(pages)) } }
        )

        // TODO: снизу \/ вот так чтобы красиво проценты считались по анимации
    }
}

@Composable
private fun PageField(
    modifier: Modifier = Modifier,
    label: String,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 4.dp),
            color = Color.Gray,
            text = label
        )

        Spacer(modifier = Modifier.height(4.dp))

        AddBookTextField(
            modifier = Modifier.height(38.dp),
            initialValue = "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = onValueChange
        )
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
        onAction = {}
    )
}
