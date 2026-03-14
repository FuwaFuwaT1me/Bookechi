package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.theme.FigmaAddBookBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
fun LibraryAddBookFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(20.dp)
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FigmaAddBookBackground)
            .clickable { onClick() }
    ) {
        Text(
            text = "+",
            modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
            color = FigmaTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun LibraryAddBookFabPreview() {
    LibraryAddBookFab(onClick = {})
}
