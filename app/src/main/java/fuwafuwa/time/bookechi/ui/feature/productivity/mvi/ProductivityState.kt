package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.mvi.api.State

data class ProductivityState(
    val booksRead: Int = 0,
    val pagesRead: Int = 0,
    val dayStreak: Int = 0,
    val averagePages: Float = 0f,
    val weeklyPagesRead: Int = 0,
    val weeklyPagesTarget: Int = 400,
    val sessions: List<DailyReadingStats> = emptyList(),
    val activityChartTab: ActivityChartTab = ActivityChartTab.MONTH,
    val currentYear: Int = 0,
    val currentMonth: Int = 0,
    /** До 3 последних прочитанных книг для веера-превью на карточке полки. */
    val shelfCovers: List<ShelfCover> = emptyList(),
    /** Суммарно страниц во всех прочитанных книгах (подзаголовок карточки полки). */
    val shelfPagesTotal: Int = 0,
    /** Последние дневные значения страниц для мини-графика на карточке журнала. */
    val journalSpark: List<Int> = emptyList(),
) : State {

    /** Истинно, когда ни одной сессии и все агрегаты нулевые — показываем «—». */
    val isEmpty: Boolean
        get() = sessions.isEmpty() &&
            booksRead == 0 &&
            pagesRead == 0 &&
            dayStreak == 0 &&
            averagePages == 0f

    /** Полка пуста — нет ни одной прочитанной книги. */
    val shelfEmpty: Boolean get() = booksRead == 0

    /** Журнал пуст — нет ни одной сессии чтения. */
    val journalEmpty: Boolean get() = sessions.isEmpty()
}

/** Минимальные данные обложки для веера-превью карточки полки. */
data class ShelfCover(
    val coverPath: String?,
    val title: String,
    val author: String,
)

enum class ActivityChartTab {
    MONTH,
    YEAR;

    companion object {

        fun fromIndex(index: Int) = entries.getOrNull(index)
    }
}
