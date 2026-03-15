package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.util.DiffConfig
import fuwafuwa.time.bookechi.base.ui.util.SimpleProgressIndicator
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressAction
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressViewModel
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellFourActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellThreeActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellTwoActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaAddBookBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaFire
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
            .padding(horizontal = 24.dp)
            .padding(top = 36.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
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
            state = state,
            onValueChange = {
                onAction(UpdateProgressAction.UpdatePageInput(it))
            }
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

        Spacer(modifier = Modifier.height(12.dp))

        PageUpdateButtons(
            onAction = onAction
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = FigmaFire,
                        fontWeight = FontWeight.Bold
                    ),
                ) {
                    append("${state.updatedInputPages - state.startPages}")
                }

                append(" страниц прочитано")
            },
            color = FigmaTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = FigmaLightGrey
        )

        Spacer(modifier = Modifier.height(8.dp))

        SimpleProgressIndicator(
            modifier = Modifier
                .height(24.dp)
                .fillMaxWidth()
            ,
            progress = 1f * state.updatedInputPages / state.book.pages,
            progressBarColor = FigmaFire,
            cornerRadius = 6.dp,
            trackColor = FigmaLightGrey,
            diffConfig = DiffConfig(
                lastProgress = 1f * state.startPages / state.book.pages,
                diffColor = FigmaActivityCellTwoActivity,
                showingPercentColor = Color.White,
                showPercent = true
            )
        )

        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
            ,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonColors(
                containerColor = FigmaActivityCellThreeActivity,
                contentColor = Color.White,
                disabledContainerColor = Color.White,
                disabledContentColor = FigmaAddBookBackground.copy(alpha = 0.5f),
            ),
            onClick = {

            }
        ) {
            Text(
                text = "Сохранить"
            )
        }
    }
}

@Composable
private fun PageInput(
    state: UpdateProgressState,
    onValueChange: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        PageNumberField(
            value = state.startPages.toString(),
            textColor = FigmaTitle.copy(alpha = 0.3f),
            onValueChange = {}
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = FigmaLightGrey
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Страница, с которой вы сегодня начали чтение",
            color = FigmaSubtitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        Icon(
            modifier = Modifier
                .size(52.dp)
                .offset(x = (-12).dp)
            ,
            imageVector = Icons.Outlined.ArrowDownward,
            contentDescription = "Arrow forward",
            tint = FigmaTitle
        )

        Spacer(Modifier.height(16.dp))

        PageNumberField(
            value = state.updatedInputPages.toString(),
            textColor = FigmaTitle,
            onValueChange = { newPageString ->
                val newPageInt = newPageString.toIntOrNull() ?: state.startPages

                if (newPageInt >= state.startPages) {
                    onValueChange(newPageInt)
                }
            }
        )
    }
}

@Composable
private fun PageNumberField(
    value: String,
    textColor: Color,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 64.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            lineHeight = 72.sp,
            textAlign = TextAlign.Start,
        ),
        maxLines = 2,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(textColor),
        singleLine = true,
        enabled = false
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

@Composable
private fun PageUpdateButtons(
    onAction: (UpdateProgressAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PageUpdateButton(
            buttonText = "+5",
            buttonColor = FigmaActivityCellTwoActivity,
            onClick = {
                onAction(UpdateProgressAction.UpdatePageInput(5))
            }
        )

        PageUpdateButton(
            buttonText = "+10",
            buttonColor = FigmaActivityCellThreeActivity,
            onClick = {
                onAction(UpdateProgressAction.UpdatePageInput(10))
            }
        )

        PageUpdateButton(
            buttonText = "+20",
            buttonColor = FigmaActivityCellFourActivity,
            onClick = {
                onAction(UpdateProgressAction.UpdatePageInput(20))
            }
        )
    }
}

@Composable
private fun PageUpdateButton(
    buttonText: String,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .width(64.dp)
            .height(40.dp)
        ,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonColors(
            containerColor = buttonColor,
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = buttonColor.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Text(
            text = buttonText,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
private fun SmallProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 54,
            updatedInputPages = 62,
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

@Preview
@Composable
private fun LargeProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 54,
            updatedInputPages = 100,
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

@Preview
@Composable
private fun OutsideProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 5,
            updatedInputPages = 10,
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
