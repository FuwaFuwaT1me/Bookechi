package fuwafuwa.time.bookechi.base.ui.keyboard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity

@Composable
fun keyboardAsState(): State<Boolean> {
    var maxKeyboardHeight by remember { mutableIntStateOf(-1) }
    var isMaxKeyboardHeightEstablished by remember { mutableStateOf(false) }

    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isImeVisible = imeBottom > 0

    if (isImeVisible && imeBottom > maxKeyboardHeight) {
        maxKeyboardHeight = imeBottom
    }
    if (maxKeyboardHeight < imeBottom && !isMaxKeyboardHeightEstablished) {
        isMaxKeyboardHeightEstablished = true
    }

    return rememberUpdatedState(imeBottom == maxKeyboardHeight)
}
