package fuwafuwa.time.bookechi.data.repository

import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.local.DeletedRecordDao
import fuwafuwa.time.bookechi.data.local.ReadingSessionDao
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.data.model.DeletedRecord
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
    private val readingSessionDao: ReadingSessionDao,
    private val bookDao: BookDao,
    private val deletedDao: DeletedRecordDao,
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    fun getAllSessions(): Flow<List<ReadingSession>> =
        readingSessionDao.getAllSessions()

    fun getSessionsForBook(bookId: Long): Flow<List<ReadingSession>> =
        readingSessionDao.getSessionsForBook(bookId)

    /** Карта bookId → последняя дата чтения (YYYY-MM-DD). */
    fun getLastReadDates(): Flow<Map<Long, String>> =
        readingSessionDao.getLastReadDates().map { list ->
            list.associate { it.bookId to it.lastReadDate }
        }

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
        readingSessionDao.insertSession(stamp(session))
    }

    suspend fun insertSessions(sessions: List<ReadingSession>) = withContext(Dispatchers.IO) {
        readingSessionDao.insertSessions(sessions.map { stamp(it) })
    }

    suspend fun updateSession(session: ReadingSession) = withContext(Dispatchers.IO) {
        readingSessionDao.updateSession(stamp(session))
    }

    suspend fun deleteSession(session: ReadingSession) = withContext(Dispatchers.IO) {
        tombstone(session.uuid)
        readingSessionDao.deleteSession(session)
    }

    suspend fun deleteSessionsForBook(bookId: Long) = withContext(Dispatchers.IO) {
        readingSessionDao.getSessionsForBookOnce(bookId).forEach { tombstone(it.uuid) }
        readingSessionDao.deleteSessionsForBook(bookId)
    }

    suspend fun deleteAllSessions() = withContext(Dispatchers.IO) {
        readingSessionDao.getAllSessionsOnce().forEach { tombstone(it.uuid) }
        readingSessionDao.deleteAllSessions()
    }

    /**
     * Проставляет sync-метки: bookUuid (резолвится из книги по bookId), детерминированный
     * uuid "${bookUuid}_${date}", время и dirty. Если uuid/bookUuid уже заданы (запись из БД) —
     * сохраняем их и только обновляем время.
     */
    private suspend fun stamp(session: ReadingSession): ReadingSession {
        val bookUuid = session.bookUuid.ifBlank { runCatching { bookDao.getBookById(session.bookId).uuid }.getOrDefault("") }
        return session.copy(
            bookUuid = bookUuid,
            uuid = session.uuid.ifBlank { "${bookUuid}_${session.date}" },
            updatedAt = System.currentTimeMillis(),
            dirty = true,
        )
    }

    private suspend fun tombstone(uuid: String) {
        if (uuid.isBlank()) return
        deletedDao.upsert(
            DeletedRecord(
                uuid = uuid,
                type = DeletedRecord.TYPE_SESSION,
                updatedAt = System.currentTimeMillis(),
                dirty = true,
            ),
        )
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
                stamp(
                    ReadingSession(
                        bookId = bookId,
                        date = dateKey,
                        pagesRead = pagesRead,
                        readingTimeMinutes = readingTimeMinutes,
                        startPage = startPage,
                        endPage = endPage
                    )
                )
            )
        } else {
            readingSessionDao.updateSession(
                stamp(
                    existing.copy(
                        pagesRead = existing.pagesRead + pagesRead,
                        readingTimeMinutes = existing.readingTimeMinutes + readingTimeMinutes,
                        startPage = if (existing.startPage == 0) startPage else minOf(existing.startPage, startPage),
                        endPage = maxOf(existing.endPage, endPage)
                    )
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
        addOrUpdateStamped(bookId, LocalDate.now().format(dateFormatter), pagesRead, readingTimeMinutes)
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
        addOrUpdateStamped(bookId, date.format(dateFormatter), pagesRead, readingTimeMinutes)
    }

    /**
     * Добавляет или обновляет сессию за день (суммируя страницы) со sync-метками.
     * Аналог [ReadingSessionDao.addOrUpdateSession], но проставляет uuid/updatedAt/dirty.
     */
    private suspend fun addOrUpdateStamped(
        bookId: Long,
        dateKey: String,
        pagesRead: Int,
        readingTimeMinutes: Int
    ) {
        val existing = readingSessionDao.getSessionForBookAndDate(bookId, dateKey)
        if (existing == null) {
            readingSessionDao.insertSession(
                stamp(
                    ReadingSession(
                        bookId = bookId,
                        date = dateKey,
                        pagesRead = pagesRead,
                        readingTimeMinutes = readingTimeMinutes,
                        startPage = 0,
                        endPage = pagesRead
                    )
                )
            )
        } else {
            readingSessionDao.updateSession(
                stamp(
                    existing.copy(
                        pagesRead = existing.pagesRead + pagesRead,
                        readingTimeMinutes = existing.readingTimeMinutes + readingTimeMinutes,
                        endPage = existing.endPage + pagesRead
                    )
                )
            )
        }
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
