package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

enum class LibraryFilter {
    All,
    Planned,
    Reading,
    Paused,
    Stopped,
    Completed,
    Favorite
}

@Composable
fun LibraryFiltersRow(
    activeFilter: LibraryFilter,
    onFilterChange: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp)
) {
    val filters = listOf(
        "Все" to LibraryFilter.All,
        "Читаю" to LibraryFilter.Reading,
        "Прочитал" to LibraryFilter.Completed,
        "В планах" to LibraryFilter.Planned,
        "Любимое" to LibraryFilter.Favorite
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(filters, key = { it.second.name }) { (label, filter) ->
            LibraryFilterChip(
                label = label,
                isSelected = activeFilter == filter,
                onClick = { onFilterChange(filter) }
            )
        }
    }
}

@Preview
@Composable
private fun LibraryFiltersRowPreview() {
    LibraryFiltersRow(
        activeFilter = LibraryFilter.Reading,
        onFilterChange = {},
        contentPadding = PaddingValues(horizontal = 16.dp)
    )
}

@Preview
@Composable
private fun LibraryFilterChipPreview() {
    LibraryFilterChip(
        label = "Читаю",
        isSelected = true,
        onClick = {}
    )
}

@Composable
fun LibraryFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (isSelected) Color(0xFFF2C6A4) else Color(0xFFF3E6E1)
    val textColor = if (isSelected) FigmaTitle else FigmaSubtitle

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
