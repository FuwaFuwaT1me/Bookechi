package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.theme.FigmaLightGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
internal fun PageInput(
    state: UpdateProgressState,
    onValueChange: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = state.startPages.toString(),
            fontSize = 64.sp,
            fontWeight = FontWeight.SemiBold,
            color = FigmaTitle.copy(alpha = 0.3f),
            lineHeight = 72.sp,
            textAlign = TextAlign.Start,
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
            state = state,
            textColor = FigmaTitle,
            enabled = true,
            onValueChange = { newPageString ->
                val newPageInt = newPageString.toIntOrNull() ?: 0

                onValueChange(
                    newPageInt.coerceIn(0, state.book.pages)
                )
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
    }
}

@Composable
internal fun PageNumberField(
    state: UpdateProgressState,
    textColor: Color,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val value = state.updatedInputPages.toString()

    Row {
        BasicTextField(
            modifier = modifier
                .weight(1f)
            ,
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
            enabled = enabled,
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

        Text(
            modifier = Modifier
                .align(Alignment.Bottom)
            ,
            text = "/${state.book.pages}",
            color = FigmaTitle.copy(alpha = 0.3f),
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 42.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PageInputPreview() {
    PageInput(
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
        onValueChange = {}
    )
}
