package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

data class BookListState(
    val books: List<Book>,
    val isLoading: Boolean = false,
    val error: String? = null,
    val gridColumnCount: Int = 1,
    val totalDaysWithStreak: Int = 0,
    val weekDayStreaks: List<DayStreak>,
    val isTodayStreak: Boolean,
    /** Прочитано страниц за текущую неделю (из сессий чтения). */
    val weeklyPagesRead: Int = 0,
    /** Цель недели по страницам. */
    val weeklyPagesTarget: Int = 400,
    /** Прочитано страниц сегодня (для нуджа «отмечено сегодня»). */
    val pagesReadToday: Int = 0,
    /**
     * Серия на паузе: есть прошлый прогресс, но сегодня/вчера не отмечено.
     * Сейчас вычисляется как (totalDaysWithStreak == 0 && books не пустые) только в Preview;
     * в основном потоке остаётся false по умолчанию.
     * TODO: compute comeback from streak history
     */
    val isComeback: Boolean = false,
) : State

data class DayStreak(
    val isStreakDay: Boolean,
    val isToday: Boolean,
)
