package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.keyboard.keyboardAsState
import fuwafuwa.time.bookechi.base.ui.textfield.BaseTextFieldColors
import fuwafuwa.time.bookechi.base.ui.textfield.BaseTextField
import fuwafuwa.time.bookechi.ui.theme.BlackLight
import fuwafuwa.time.bookechi.ui.theme.BlueMain

@Composable
fun AddBookTextField(
    modifier: Modifier,
    state: TextFieldState,
    hint: String = "",
) {
    val isKeyboardOpen by keyboardAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(isKeyboardOpen) {
        Log.d("ANIME", "isKeyboardOpen: $isKeyboardOpen")
        if (!isKeyboardOpen) {
            focusManager.clearFocus(force = true)
        }
    }

    BaseTextField(
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
        cornerRadius = 8.dp,
        interactionSource = interactionSource,
        focusRequester = focusRequester,
    )
}

@Preview
@Composable
private fun AddBookTextFieldPreview() {
    AddBookTextField(
        modifier = Modifier,
        state = TextFieldState(),
        hint = "Hint",
    )
}
