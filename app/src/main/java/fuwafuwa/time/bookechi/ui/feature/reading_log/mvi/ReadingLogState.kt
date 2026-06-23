package fuwafuwa.time.bookechi.ui.feature.reading_log.mvi

import fuwafuwa.time.bookechi.mvi.api.State
import java.time.LocalDate

/** Одна запись журнала (сессия чтения за день по книге). */
data class SessionLogItem(
    val sessionId: Long,
    val bookId: Long,
    val title: String,
    val author: String,
    val coverPath: String?,
    val fromPage: Int,
    val toPage: Int,
    val totalPages: Int,
    val minutes: Int,
    val date: LocalDate,
    /** Сколько дней назад (0 = сегодня). */
    val dayOffset: Int,
    /** Последняя сессия книги, совпадающая с её актуальным прогрессом. */
    val isCurrent: Boolean,
) {
    val pagesRead: Int get() = toPage - fromPage
}

/** Сводка за период для шапки полного журнала. */
data class PeriodSummary(
    val sessions: Int = 0,
    val pages: Int = 0,
    val minutes: Int = 0,
    /** Дельта к прошлому периоду в % (null — нет данных за прошлый период или «всё время»). */
    val deltaPercent: Int? = null,
    /** Для «всё время»: число книг и метка «с {месяц год}». */
    val booksCount: Int = 0,
    val sinceLabel: String = "",
)

data class ReadingLogState(
    val bookId: Long = -1L,
    val isBookScope: Boolean = false,
    val bookTitle: String = "",
    val items: List<SessionLogItem> = emptyList(),
    val weekSummary: PeriodSummary = PeriodSummary(),
    val monthSummary: PeriodSummary = PeriodSummary(),
    val allTimeSummary: PeriodSummary = PeriodSummary(),
    val isLoading: Boolean = true,
) : State
