package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.keyboard.keyboardAsState
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
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
        onSaveBookAction = {
            viewModel.sendAction(AddBookAction.SaveBook)
        },
        onAddBookCover = { uri ->
            viewModel.sendAction(AddBookAction.LoadBookCover(uri))
        },
        onClearBookCover = {
            viewModel.sendAction(AddBookAction.ClearBookCover)
        },
        onUpdateAuthor = {
            viewModel.sendAction(AddBookAction.UpdateBookDetails(state.copy(bookAuthor = it)))
        },
        onUpdateName = {
            viewModel.sendAction(AddBookAction.UpdateBookDetails(state.copy(bookName = it)))
        },
        onUpdateCurrentPage = {
            viewModel.sendAction(AddBookAction.UpdateBookDetails(state.copy(bookCurrentPage = it)))
        },
        onUpdateAllPages = {
            viewModel.sendAction(AddBookAction.UpdateBookDetails(state.copy(bookPages = it)))
        },
        onNavigateBack = {
            viewModel.sendNavigationEvent(BaseNavigationEvent.NavigateBack)
        }
    )
}

@Composable
private fun AddBookScreenPrivate(
    state: AddBookState,
    onSaveBookAction: () -> Unit,
    onAddBookCover: (Uri?) -> Unit,
    onClearBookCover: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateAuthor: (String) -> Unit,
    onUpdateCurrentPage: (Int) -> Unit,
    onUpdateAllPages: (Int) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Header(
                state = state,
                onClearBookCover = onClearBookCover,
                onNavigateBack = onNavigateBack
            )

            BookPager(
                modifier = Modifier
                ,
                state = state,
                onAddBookCover = onAddBookCover,
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFields(
                state = state,
                onUpdateName = onUpdateName,
                onUpdateAuthor = onUpdateAuthor,
                onUpdateCurrentPage = onUpdateCurrentPage,
                onUpdateAllPages = onUpdateAllPages,
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(46.dp)
                .align(Alignment.BottomCenter),
            colors = ButtonColors(
                containerColor = BlueMain,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                onSaveBookAction()
            },
        ) {
            Text("Save book")
        }
    }
}

@Composable
private fun Header(
    state: AddBookState,
    onClearBookCover: () -> Unit,
    onNavigateBack: () -> Unit,
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
            onClick = onNavigateBack
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                ,
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
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Button(
                    modifier = Modifier
                        .size(32.dp)
                    ,
                    colors = ButtonColors(
                        containerColor = BlueMain,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                        ,
                        imageVector = Icons.Filled.MoreVert,
                        tint = Color.White,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Clear cover") },
                        onClick = {
                            showMenu = false
                            onClearBookCover()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InputFields(
    state: AddBookState,
    onUpdateName: (String) -> Unit,
    onUpdateAuthor: (String) -> Unit,
    onUpdateCurrentPage: (Int) -> Unit,
    onUpdateAllPages: (Int) -> Unit,
) {
    val bookNameState = rememberTextFieldState(state.bookName)
    val bookAuthorState = rememberTextFieldState(state.bookAuthor)

    LaunchedEffect(bookNameState) {
        snapshotFlow { bookNameState.text.toString() }
            .collect { newName ->
                if (newName != state.bookName) {
                    onUpdateName(newName)
                }
            }
    }

    LaunchedEffect(bookAuthorState) {
        snapshotFlow { bookAuthorState.text.toString() }
            .collect { newAuthor ->
                if (newAuthor != state.bookAuthor) {
                    onUpdateAuthor(newAuthor)
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SuperLightGray)
            .padding(start = 8.dp, end = 8.dp, top = 16.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.size(16.dp))

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

            Pages(
                state = state,
                onUpdateCurrentPage = onUpdateCurrentPage,
                onUpdateAllPages = onUpdateAllPages,
            )
        }
    }
}

@Composable
private fun Pages(
    state: AddBookState,
    onUpdateCurrentPage: (Int) -> Unit,
    onUpdateAllPages: (Int) -> Unit,
) {
    val currentPageState = rememberTextFieldState("")
    val allPagesState = rememberTextFieldState("")

    LaunchedEffect(currentPageState) {
        snapshotFlow { currentPageState.text.toString().toIntOrNull() }
            .collect { newCurrentPage ->
                if (newCurrentPage != null && newCurrentPage != state.bookCurrentPage) {
                    onUpdateCurrentPage(newCurrentPage)
                }
            }
    }

    LaunchedEffect(allPagesState) {
        snapshotFlow { allPagesState.text.toString().toIntOrNull() }
            .collect { newAllPages ->
                if (newAllPages != null && newAllPages != state.bookPages) {
                    onUpdateAllPages(newAllPages)
                }
            }
    }

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
        onSaveBookAction = {},
        onAddBookCover = {},
        onClearBookCover = {},
        onUpdateAuthor = {},
        onUpdateName = {},
        onUpdateCurrentPage = {},
        onUpdateAllPages = {},
        onNavigateBack = {},
    )
}
