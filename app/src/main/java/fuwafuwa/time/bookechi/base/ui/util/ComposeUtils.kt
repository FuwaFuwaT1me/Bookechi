package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun Modifier.optionalClickable(onClick: (() -> Unit)?) =
    if (onClick != null) {
        clickable(onClick = onClick)
    } else {
        this
    }

@Composable
fun Modifier.optionalFocusRequester(focusRequester: FocusRequester?) =
    if (focusRequester != null) {
        focusRequester(focusRequester)
    } else {
        this
    }
