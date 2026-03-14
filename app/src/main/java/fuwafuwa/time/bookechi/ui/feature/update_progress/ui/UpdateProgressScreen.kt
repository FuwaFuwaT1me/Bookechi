package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressAction
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressViewModel
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaBackgroundStroke
import fuwafuwa.time.bookechi.ui.theme.FigmaLightGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProgressScreen(
    val book: Book
) : Screen

@Composable
fun UpdateProgressScreen(
    viewModel: UpdateProgressViewModel,
) {
    val state by viewModel.model.state.collectAsState()

    UpdateProgressScreenContent(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun UpdateProgressScreenContent(
    state: UpdateProgressState,
    onAction: (UpdateProgressAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBackground)
            .padding(horizontal = 24.dp, vertical = 36.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Где остановились\nсегодня?",
            color = FigmaTitle,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = state.book.name,
            color = FigmaSubtitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(52.dp))

        PageInput(
            value = state.pageInput,
            onValueChange = { onAction(UpdateProgressAction.UpdatePageInput(it)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = FigmaLightGrey
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Введите страницу, на которой сегодня закончили чтение",
            color = FigmaSubtitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PageInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 64.sp,
            fontWeight = FontWeight.SemiBold,
            color = FigmaTitle,
            lineHeight = 72.sp
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(FigmaTitle),
        singleLine = true
    ) { innerTextField ->
        if (value.isBlank()) {
            Text(
                text = "0",
                color = FigmaTitle.copy(alpha = 0.3f),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 72.sp
            )
        }
        innerTextField()
    }
}

@Preview
@Composable
private fun UpdateProgressScreenPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            pageInput = "54",
            book = Book(
                name = "Название книги очень длинное",
                author = "Murakami",
                coverPath = null,
                pages = 256,
                currentPage = 40
            )
        ),
        onAction = {}
    )
}
