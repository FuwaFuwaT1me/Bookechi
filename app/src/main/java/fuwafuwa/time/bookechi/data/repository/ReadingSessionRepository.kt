package fuwafuwa.time.bookechi.data.repository

import fuwafuwa.time.bookechi.data.local.ReadingSessionDao
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.data.model.ReadingSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class ReadingSessionRepository(
    private val readingSessionDao: ReadingSessionDao
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    fun getSessionsForBook(bookId: Long): Flow<List<ReadingSession>> =
        readingSessionDao.getSessionsForBook(bookId)

    fun getSessionsForDate(date: LocalDate): Flow<List<ReadingSession>> =
        readingSessionDao.getSessionsForDate(date.format(dateFormatter))

    fun getDailyStatsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyReadingStats>> =
        readingSessionDao.getDailyStatsForPeriod(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        )

    fun getDailyStatsForMonth(year: Int, month: Int): Flow<List<DailyReadingStats>> {
        val yearMonth = String.format("%04d-%02d", year, month)
        return readingSessionDao.getDailyStatsForMonth(yearMonth)
    }

    fun getDailyStatsForYear(year: Int): Flow<List<DailyReadingStats>> =
        readingSessionDao.getDailyStatsForYear(year.toString())

    suspend fun getTotalPagesReadForBook(bookId: Long): Int = withContext(Dispatchers.IO) {
        readingSessionDao.getTotalPagesReadForBook(bookId) ?: 0
    }

    suspend fun getTotalPagesReadForDate(date: LocalDate): Int = withContext(Dispatchers.IO) {
        readingSessionDao.getTotalPagesReadForDate(date.format(dateFormatter)) ?: 0
    }

    suspend fun insertSession(session: ReadingSession) = withContext(Dispatchers.IO) {
        readingSessionDao.insertSession(session)
    }

    suspend fun insertSessions(sessions: List<ReadingSession>) = withContext(Dispatchers.IO) {
        readingSessionDao.insertSessions(sessions)
    }

    suspend fun updateSession(session: ReadingSession) = withContext(Dispatchers.IO) {
        readingSessionDao.updateSession(session)
    }

    suspend fun deleteSession(session: ReadingSession) = withContext(Dispatchers.IO) {
        readingSessionDao.deleteSession(session)
    }

    suspend fun deleteSessionsForBook(bookId: Long) = withContext(Dispatchers.IO) {
        readingSessionDao.deleteSessionsForBook(bookId)
    }

    /**
     * Логирует прочтение страниц за сегодня
     */
    suspend fun logReadingProgress(
        bookId: Long,
        pagesRead: Int,
        readingTimeMinutes: Int = 0
    ) = withContext(Dispatchers.IO) {
        val today = LocalDate.now().format(dateFormatter)
        readingSessionDao.addOrUpdateSession(bookId, today, pagesRead, readingTimeMinutes)
    }

    /**
     * Логирует прочтение страниц за указанную дату
     */
    suspend fun logReadingProgressForDate(
        bookId: Long,
        date: LocalDate,
        pagesRead: Int,
        readingTimeMinutes: Int = 0
    ) = withContext(Dispatchers.IO) {
        readingSessionDao.addOrUpdateSession(
            bookId,
            date.format(dateFormatter),
            pagesRead,
            readingTimeMinutes
        )
    }

    /**
     * Создает ReadingSession с правильным форматом даты
     */
    fun createSession(
        bookId: Long,
        date: LocalDate,
        pagesRead: Int,
        readingTimeMinutes: Int = 0,
        startPage: Int = 0,
        endPage: Int = 0
    ): ReadingSession = ReadingSession(
        bookId = bookId,
        date = date.format(dateFormatter),
        pagesRead = pagesRead,
        readingTimeMinutes = readingTimeMinutes,
        startPage = startPage,
        endPage = endPage
    )
}


