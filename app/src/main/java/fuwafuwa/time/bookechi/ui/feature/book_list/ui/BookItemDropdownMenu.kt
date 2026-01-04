package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BlackLight

@Composable
fun BookItemDropdownMenu(
    showMenu: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteBookClick: () -> Unit,
) {
    DropdownMenu(
        expanded = showMenu,
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "Delete",
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
            onClick = onDeleteBookClick
        )
    }
}
