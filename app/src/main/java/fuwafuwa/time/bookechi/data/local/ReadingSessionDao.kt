package fuwafuwa.time.bookechi.data.local

import androidx.room.*
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.data.model.ReadingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {

    @Query("SELECT * FROM ReadingSession WHERE bookId = :bookId ORDER BY date DESC")
    fun getSessionsForBook(bookId: Long): Flow<List<ReadingSession>>

    @Query("SELECT * FROM ReadingSession WHERE date = :date")
    fun getSessionsForDate(date: String): Flow<List<ReadingSession>>

    @Query("SELECT * FROM ReadingSession WHERE bookId = :bookId AND date = :date")
    suspend fun getSessionForBookAndDate(bookId: Long, date: String): ReadingSession?

    @Query("""
        SELECT 
            date,
            SUM(pagesRead) as totalPagesRead,
            SUM(readingTimeMinutes) as totalReadingTimeMinutes,
            COUNT(DISTINCT bookId) as booksRead
        FROM ReadingSession
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date
    """)
    fun getDailyStatsForPeriod(startDate: String, endDate: String): Flow<List<DailyReadingStats>>

    @Query("""
        SELECT 
            date,
            SUM(pagesRead) as totalPagesRead,
            SUM(readingTimeMinutes) as totalReadingTimeMinutes,
            COUNT(DISTINCT bookId) as booksRead
        FROM ReadingSession
        WHERE date LIKE :yearMonth || '%'
        GROUP BY date
        ORDER BY date
    """)
    fun getDailyStatsForMonth(yearMonth: String): Flow<List<DailyReadingStats>>

    @Query("""
        SELECT 
            date,
            SUM(pagesRead) as totalPagesRead,
            SUM(readingTimeMinutes) as totalReadingTimeMinutes,
            COUNT(DISTINCT bookId) as booksRead
        FROM ReadingSession
        WHERE date LIKE :year || '%'
        GROUP BY date
        ORDER BY date
    """)
    fun getDailyStatsForYear(year: String): Flow<List<DailyReadingStats>>

    @Query("SELECT SUM(pagesRead) FROM ReadingSession WHERE bookId = :bookId")
    suspend fun getTotalPagesReadForBook(bookId: Long): Int?

    @Query("SELECT SUM(pagesRead) FROM ReadingSession WHERE date = :date")
    suspend fun getTotalPagesReadForDate(date: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ReadingSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<ReadingSession>)

    @Update
    suspend fun updateSession(session: ReadingSession)

    @Delete
    suspend fun deleteSession(session: ReadingSession)

    @Query("DELETE FROM ReadingSession WHERE bookId = :bookId")
    suspend fun deleteSessionsForBook(bookId: Long)

    @Query("DELETE FROM ReadingSession")
    suspend fun deleteAllSessions()

    /**
     * Добавляет или обновляет сессию чтения за день.
     * Если запись за этот день уже существует - добавляет страницы к существующей.
     */
    @Transaction
    suspend fun addOrUpdateSession(bookId: Long, date: String, pagesRead: Int, readingTimeMinutes: Int = 0) {
        val existing = getSessionForBookAndDate(bookId, date)
        if (existing != null) {
            updateSession(
                existing.copy(
                    pagesRead = existing.pagesRead + pagesRead,
                    readingTimeMinutes = existing.readingTimeMinutes + readingTimeMinutes,
                    endPage = existing.endPage + pagesRead
                )
            )
        } else {
            insertSession(
                ReadingSession(
                    bookId = bookId,
                    date = date,
                    pagesRead = pagesRead,
                    readingTimeMinutes = readingTimeMinutes,
                    startPage = 0,
                    endPage = pagesRead
                )
            )
        }
    }
}


