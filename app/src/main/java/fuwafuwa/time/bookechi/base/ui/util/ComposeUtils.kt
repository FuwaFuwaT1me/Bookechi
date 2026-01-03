package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.optionalClickable(onClick: (() -> Unit)?) =
    if (onClick != null) {
        clickable(onClick = onClick)
    } else {
        this
    }
