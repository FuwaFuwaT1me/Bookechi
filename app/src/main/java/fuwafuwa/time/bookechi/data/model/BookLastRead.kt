package fuwafuwa.time.bookechi.data.model

/** Проекция: последняя дата чтения (YYYY-MM-DD) по каждой книге. */
data class BookLastRead(
    val bookId: Long,
    val lastReadDate: String,
)
