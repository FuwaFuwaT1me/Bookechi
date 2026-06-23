package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.mvi.api.State
import java.time.LocalDate

/** Книга на полке прочитанного. */
data class ShelfBookItem(
    val bookId: Long,
    val title: String,
    val author: String,
    val coverPath: String?,
    val favorite: Boolean,
    val finishedAt: LocalDate?,
)

/** Полка одного месяца. monthValue 0 — «без даты». */
data class ShelfMonth(
    val year: Int,
    val monthValue: Int,
    val books: List<ShelfBookItem>,
)

data class ShelfState(
    val months: List<ShelfMonth> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
) : State
