package fuwafuwa.time.bookechi.data.repository

import fuwafuwa.time.bookechi.data.local.ReadingSessionDao
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.data.model.ReadingSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class CurrentStreak(
    val totalDays: Int,
    val isTodayStreak: Boolean,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)

data class WeeklyStreak(
    val totalDays: Int,
    val isTodayStreak: Boolean,
    val days: List<WeekStreakDay>
)

data class WeekStreakDay(
    val date: LocalDate,
    val isStreakDay: Boolean,
    val isToday: Boolean
)

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

    fun getDailyStatsForCurrentWeek(
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        clock: Clock = Clock.systemDefaultZone()
    ): Flow<List<DailyReadingStats>> {
        val today = LocalDate.now(clock)
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        val endOfWeek = startOfWeek.plusDays(6)
        return getDailyStatsForPeriod(startOfWeek, endOfWeek)
    }

    fun getCurrentStreak(clock: Clock = Clock.systemDefaultZone()): Flow<CurrentStreak> {
        val today = LocalDate.now(clock)
        val todayKey = today.format(dateFormatter)
        return readingSessionDao.getActiveSessionDatesUpTo(todayKey).map { dates ->
            calculateCurrentStreak(dates, today)
        }
    }

    fun getWeeklyStreak(
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        clock: Clock = Clock.systemDefaultZone()
    ): Flow<WeeklyStreak> {
        val today = LocalDate.now(clock)
        val todayKey = today.format(dateFormatter)
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))

        return readingSessionDao.getActiveSessionDatesUpTo(todayKey).map { dates ->
            val streak = calculateCurrentStreak(dates, today)
            WeeklyStreak(
                totalDays = streak.totalDays,
                isTodayStreak = streak.isTodayStreak,
                days = buildWeekStreakDays(startOfWeek, today, streak.startDate, streak.endDate)
            )
        }
    }

    fun getCurrentStreakDays(clock: Clock = Clock.systemDefaultZone()): Flow<Int> =
        getCurrentStreak(clock).map { it.totalDays }

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

    suspend fun deleteAllSessions() = withContext(Dispatchers.IO) {
        readingSessionDao.deleteAllSessions()
    }

    /**
     * Записывает прогресс чтения, игнорируя нулевые/отрицательные изменения.
     */
    suspend fun recordReadingProgress(
        bookId: Long,
        startPage: Int,
        endPage: Int,
        date: LocalDate = LocalDate.now(),
        readingTimeMinutes: Int = 0
    ) = withContext(Dispatchers.IO) {
        val pagesRead = endPage - startPage
        if (pagesRead <= 0) return@withContext

        val dateKey = date.format(dateFormatter)
        val existing = readingSessionDao.getSessionForBookAndDate(bookId, dateKey)
        if (existing == null) {
            readingSessionDao.insertSession(
                ReadingSession(
                    bookId = bookId,
                    date = dateKey,
                    pagesRead = pagesRead,
                    readingTimeMinutes = readingTimeMinutes,
                    startPage = startPage,
                    endPage = endPage
                )
            )
        } else {
            readingSessionDao.updateSession(
                existing.copy(
                    pagesRead = existing.pagesRead + pagesRead,
                    readingTimeMinutes = existing.readingTimeMinutes + readingTimeMinutes,
                    startPage = if (existing.startPage == 0) startPage else minOf(existing.startPage, startPage),
                    endPage = maxOf(existing.endPage, endPage)
                )
            )
        }
    }

    /**
     * Логирует прочтение страниц за сегодня
     */
    suspend fun logReadingProgress(
        bookId: Long,
        pagesRead: Int,
        readingTimeMinutes: Int = 0
    ) = withContext(Dispatchers.IO) {
        if (pagesRead <= 0) return@withContext
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
        if (pagesRead <= 0) return@withContext
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

    private fun calculateCurrentStreak(dates: List<String>, today: LocalDate): CurrentStreak {
        if (dates.isEmpty()) {
            return CurrentStreak(
                totalDays = 0,
                isTodayStreak = false,
                startDate = null,
                endDate = null
            )
        }

        val todayKey = today.format(dateFormatter)
        val hasToday = dates.firstOrNull() == todayKey
        val startDate = if (hasToday) today else today.minusDays(1)

        var streak = 0
        var expectedDate = startDate

        for (dateString in dates) {
            val date = LocalDate.parse(dateString)
            if (date == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else if (date.isBefore(expectedDate)) {
                break
            }
        }

        if (streak == 0) {
            return CurrentStreak(
                totalDays = 0,
                isTodayStreak = false,
                startDate = null,
                endDate = null
            )
        }

        val endDate = if (hasToday) today else today.minusDays(1)
        val streakStart = endDate.minusDays(streak.toLong() - 1)

        return CurrentStreak(
            totalDays = streak,
            isTodayStreak = hasToday,
            startDate = streakStart,
            endDate = endDate
        )
    }

    private fun buildWeekStreakDays(
        startOfWeek: LocalDate,
        today: LocalDate,
        streakStart: LocalDate?,
        streakEnd: LocalDate?
    ): List<WeekStreakDay> = (0..6).map { offset ->
        val date = startOfWeek.plusDays(offset.toLong())
        WeekStreakDay(
            date = date,
            isStreakDay = streakStart != null &&
                streakEnd != null &&
                !date.isBefore(streakStart) &&
                !date.isAfter(streakEnd),
            isToday = date == today
        )
    }
}
