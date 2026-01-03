package fuwafuwa.time.bookechi.base.ui.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    hint: String = "",
    textFieldColors: BaseTextFieldColors = BaseTextFieldColors(
        hintColor = Color(0xFFBDBDBD),
        textColor = Color.Black,
        containerColor = Color.White,
        indicatorColor = Color(0xFF6200EE),
        borderColor = Color.Transparent,
    ),
    cornerRadius: Dp = 28.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .background(
                color = textFieldColors.containerColor,
                shape = shape
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) textFieldColors.indicatorColor else textFieldColors.borderColor,
                shape = shape
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = textFieldColors.textColor,
                fontSize = 16.sp
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (state.text.isEmpty()) {
                        Text(
                            text = hint,
                            color = textFieldColors.hintColor,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            },
            interactionSource = interactionSource,
        )
    }
}

@Preview
@Composable
private fun SimpleTextFieldPreview() {
    SimpleTextField(
        state = remember { TextFieldState() },
        modifier = Modifier.padding(16.dp)
    )
}
