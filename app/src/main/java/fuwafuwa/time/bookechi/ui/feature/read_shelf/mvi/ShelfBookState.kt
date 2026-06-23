package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.api.State
import java.time.LocalDate

data class ShelfBookState(
    val bookId: Long = -1L,
    val title: String = "",
    val author: String = "",
    val coverPath: String? = null,
    val favorite: Boolean = false,
    val pages: Int = 0,
    val sessionsCount: Int = 0,
    val totalMinutes: Int = 0,
    val startedAt: LocalDate? = null,
    val finishedAt: LocalDate? = null,
    val daysCount: Int = 0,
    /** Накопленный прогресс по сессиям (доли 0..1), по датам по возрастанию. */
    val progress: List<Float> = emptyList(),
    /** Рекорд страниц за сессию. */
    val recordPages: Int = 0,
    /** Оценка книги (0 — не оценена). */
    val rating: Int = 0,
    /** Открыта ли шторка проставления оценки. */
    val isRatingSheetOpen: Boolean = false,
    /** Книга для перехода на полную карточку. */
    val book: Book? = null,
    val isLoading: Boolean = true,
) : State
