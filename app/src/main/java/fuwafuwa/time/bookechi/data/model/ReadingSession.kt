package fuwafuwa.time.bookechi.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
)

/**
 * Данные для отображения в чарте активности
 */
data class ReadingActivityData(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val pagesRead: Int,
    val intensity: ActivityIntensity
)

/**
 * Уровень интенсивности чтения для цветовой градации
 */
enum class ActivityIntensity {
    NONE,      // Не читал
    LOW,       // 1-10 страниц
    MEDIUM,    // 11-30 страниц
    HIGH,      // 31-60 страниц
    VERY_HIGH  // 60+ страниц
}

/**
 * Определяет интенсивность по количеству страниц (абсолютные пороги)
 */
fun getActivityIntensity(pagesRead: Int): ActivityIntensity {
    return when {
        pagesRead <= 0 -> ActivityIntensity.NONE
        pagesRead <= 10 -> ActivityIntensity.LOW
        pagesRead <= 30 -> ActivityIntensity.MEDIUM
        pagesRead <= 60 -> ActivityIntensity.HIGH
        else -> ActivityIntensity.VERY_HIGH
    }
}

/**
 * Определяет интенсивность относительно максимального значения (как на GitHub).
 * Разбивает на квартили: 0%, 1-25%, 26-50%, 51-75%, 76-100% от максимума.
 */
fun getRelativeActivityIntensity(pagesRead: Int, maxPages: Int): ActivityIntensity {
    if (pagesRead <= 0) return ActivityIntensity.NONE
    if (maxPages <= 0) return ActivityIntensity.NONE
    
    val percentage = (pagesRead.toFloat() / maxPages.toFloat() * 100).toInt()
    
    return when {
        percentage <= 25 -> ActivityIntensity.LOW
        percentage <= 50 -> ActivityIntensity.MEDIUM
        percentage <= 75 -> ActivityIntensity.HIGH
        else -> ActivityIntensity.VERY_HIGH
    }
}

