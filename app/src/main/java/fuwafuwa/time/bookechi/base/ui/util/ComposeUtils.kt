package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun Modifier.optionalClickable(onClick: (() -> Unit)?) =
    if (onClick != null) {
        then(clickable(onClick = onClick))
    } else {
        this
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.optionalDetectTapGestures(
    onClick: (() -> Unit)?,
    onLongTap: (() -> Unit)?,
): Modifier {
    if (onClick == null && onLongTap == null) {
        return this
    }

    return then(
        combinedClickable(
            onClick = onClick ?: {},
            onLongClick = onLongTap
        )
    )
}

@Composable
fun Modifier.optionalFocusRequester(focusRequester: FocusRequester?) =
    if (focusRequester != null) {
        then(focusRequester(focusRequester))
    } else {
        this
    }
