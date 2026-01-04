package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookAction
import fuwafuwa.time.bookechi.ui.theme.BlackLight
import fuwafuwa.time.bookechi.ui.theme.BlueMain

@Composable
fun BoxScope.AddBookDropdownMenu(
    showMenu: Boolean,
    onShowMenuChange: (Boolean) -> Unit,
    onAction: (AddBookAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.CenterEnd)
    ) {
        Button(
            modifier = Modifier
                .size(32.dp),
            colors = ButtonColors(
                containerColor = BlueMain,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp),
            onClick = { onShowMenuChange(true) }
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp),
                imageVector = Icons.Filled.MoreVert,
                tint = Color.White,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = showMenu,
            shape = RoundedCornerShape(12.dp),
            onDismissRequest = { onShowMenuChange(false) }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Clear cover",
                        color = BlackLight,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        tint = BlackLight,
                        contentDescription = null
                    )
                },
                onClick = {
                    onShowMenuChange(false)
                    onAction(AddBookAction.ClearBookCover)
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAddBookDropdownMenuClosed() {
    Box {
        AddBookDropdownMenu(
            showMenu = false,
            onShowMenuChange = {},
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun PreviewAddBookDropdownMenuOpened() {
    Box {
        AddBookDropdownMenu(
            showMenu = true,
            onShowMenuChange = {},
            onAction = {}
        )
    }
}
