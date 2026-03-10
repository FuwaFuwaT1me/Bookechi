package fuwafuwa.time.bookechi.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import fuwafuwa.time.bookechi.base.time.Date
import java.time.LocalDate

/**
 * Сущность для хранения статистики чтения по дням.
 * Каждая запись представляет один день чтения конкретной книги.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("bookId"),
        Index(value = ["bookId", "date"], unique = true)
    ]
)
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    /** Дата в формате YYYY-MM-DD */
    val date: String,
    /** Количество прочитанных страниц за этот день */
    val pagesRead: Int,
    /** Время чтения в минутах (опционально) */
    val readingTimeMinutes: Int = 0,
    /** Начальная страница на начало сессии */
    val startPage: Int = 0,
    /** Конечная страница на конец сессии */
    val endPage: Int = 0
)

/**
 * Агрегированная статистика чтения за день (по всем книгам)
 */
data class DailyReadingStats(
    val date: String,
    val totalPagesRead: Int,
    val totalReadingTimeMinutes: Int,
    val booksRead: Int
) {
    val localDate: LocalDate get() = LocalDate.parse(date)
    val dateInfo: Date get() = Date.from(localDate)
}
