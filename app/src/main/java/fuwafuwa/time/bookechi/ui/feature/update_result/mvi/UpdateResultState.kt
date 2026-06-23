package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import fuwafuwa.time.bookechi.data.repository.WeekStreakDay
import fuwafuwa.time.bookechi.mvi.api.State

data class UpdateResultState(
    val pagesDelta: Int,
    val startPages: Int,
    val updatedPages: Int,
    val allBookPages: Int,
    val newStreakCount: Int,
    /** Идентификатор книги (для сохранения оценки/заметки на экране «Книга прочитана»). */
    val bookId: Long = -1L,
    /** Какая это по счёту прочитанная книга в текущем году (для плашки на финале). */
    val booksThisYear: Int = 0,
    /** Минуты, потраченные за эту сессию (для сводки экрана результатов). */
    val readingTimeMinutes: Int = 0,
    /** Обложка/метаданные книги для героя экрана результатов. */
    val bookName: String = "",
    val bookAuthor: String = "",
    val coverPath: String? = null,
    /** Дни недели (Пн–Вс) для перебивки «Серия продлена». */
    val weekDays: List<WeekStreakDay> = emptyList(),
    /** Показывать ли полноэкранную перебивку про серию (только при факте продления). */
    val showStreakIntro: Boolean = false,
    // TODO: persist rating & note (needs schema) — пока живут только в State этой фичи.
    val rating: Int = 0,
    val note: String = "",
) : State {

    /** Книга дочитана: текущая страница достигла/превысила объём книги. */
    val isFinished: Boolean
        get() = allBookPages > 0 && updatedPages >= allBookPages
}
