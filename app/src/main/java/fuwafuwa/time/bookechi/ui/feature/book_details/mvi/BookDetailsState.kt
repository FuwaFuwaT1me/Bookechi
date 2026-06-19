package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State

/**
 * Цитата/заметка к книге.
 * TODO: persist quotes & rating (needs Book/schema extension).
 */
data class BookQuote(
    val text: String,
    val page: Int,
)

data class BookDetailsState(
    val book: Book?,
    val isLoading: Boolean = false,
    val error: String? = null,
    /**
     * Страницы за последние ~10 дней (для спарклайна истории чтения).
     * Заполняется из ReadingSessionRepository.getSessionsForBook(bookId).
     */
    val recentSessionPages: List<Int> = emptyList(),
    /**
     * Цитаты/заметки. На реальных данных пусто — Book не хранит цитаты.
     * TODO: persist quotes & rating (needs Book/schema extension).
     */
    val quotes: List<BookQuote> = emptyList(),
    /**
     * Оценка книги 0..5. На реальных данных всегда 0 — Book не хранит рейтинг.
     * TODO: persist quotes & rating (needs Book/schema extension).
     */
    val rating: Int = 0,
    /** Открыт ли лист редактирования метаданных книги. */
    val isEditing: Boolean = false,
) : State
