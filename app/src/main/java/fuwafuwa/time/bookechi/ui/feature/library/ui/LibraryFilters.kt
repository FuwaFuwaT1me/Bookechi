package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.FilterChip
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Фильтры библиотеки. Paused/Dropped оставлены в enum для совместимости со
 * статусами книг, но НЕ выводятся в ряду фильтров (см. [visibleFilters]).
 */
enum class LibraryFilter(val label: String) {
    All("Все"),
    Planned("В планах"),
    Reading("Читаю"),
    Completed("Прочитано"),
    Favorite("Любимое"),
    Paused("Приостановлено"),
    Dropped("Брошено"),
}

/** Ровно 5 чипов в порядке макета. */
val visibleFilters: List<LibraryFilter> = listOf(
    LibraryFilter.All,
    LibraryFilter.Planned,
    LibraryFilter.Reading,
    LibraryFilter.Completed,
    LibraryFilter.Favorite,
)

fun List<Book>.filteredBy(filter: LibraryFilter): List<Book> = when (filter) {
    LibraryFilter.All -> this
    LibraryFilter.Reading -> filter { it.readingStatus == ReadingStatus.Reading }
    LibraryFilter.Completed -> filter { it.readingStatus == ReadingStatus.Completed }
    LibraryFilter.Planned -> filter { it.readingStatus == ReadingStatus.Planned }
    LibraryFilter.Favorite -> filter { it.isFavorite }
    LibraryFilter.Paused -> filter { it.readingStatus == ReadingStatus.Paused }
    LibraryFilter.Dropped -> filter { it.readingStatus == ReadingStatus.Dropped }
}

@Composable
fun LibraryFiltersRow(
    activeFilter: LibraryFilter,
    onFilterChange: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp)
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        contentPadding = contentPadding
    ) {
        items(visibleFilters, key = { it.name }) { filter ->
            FilterChip(
                text = filter.label,
                selected = activeFilter == filter,
                onClick = { onFilterChange(filter) },
            )
        }
    }
}

@Preview(name = "LibraryFilters Light", showBackground = true, backgroundColor = 0xFFFFF9F6)
@Composable
private fun LibraryFiltersRowPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryFiltersRow(
                activeFilter = LibraryFilter.Reading,
                onFilterChange = {},
                contentPadding = PaddingValues(Spacing.lg)
            )
        }
    }
}

@Preview(name = "LibraryFilters Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun LibraryFiltersRowPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            LibraryFiltersRow(
                activeFilter = LibraryFilter.All,
                onFilterChange = {},
                contentPadding = PaddingValues(Spacing.lg)
            )
        }
    }
}
