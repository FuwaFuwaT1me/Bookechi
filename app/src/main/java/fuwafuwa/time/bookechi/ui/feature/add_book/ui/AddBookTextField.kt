package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.textfield.BaseTextFieldColors
import fuwafuwa.time.bookechi.base.ui.textfield.SimpleTextField
import fuwafuwa.time.bookechi.ui.theme.BlackLight
import fuwafuwa.time.bookechi.ui.theme.BlackLight2
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import fuwafuwa.time.bookechi.ui.theme.BlueMainDark

@Composable
fun AddBookTextField(
    modifier: Modifier,
    state: TextFieldState,
    hint: String = "",
) {
    SimpleTextField(
        modifier = modifier
            .height(42.dp),
        state = state,
        hint = hint,
        textFieldColors = BaseTextFieldColors(
            hintColor = Color(0xFFBDBDBD),
            textColor = BlackLight,
            containerColor = Color.White,
            focusedBorderColor = BlueMain,
            borderColor = Color.Transparent,
            indicatorColor = BlueMain,
        ),
        cornerRadius = 8.dp
    )
}

@Preview
@Composable
private fun AddBookTextFieldPreview() {
    AddBookTextField(
        modifier = Modifier,
        state = TextFieldState(),
        hint = "Hint"
    )
}
