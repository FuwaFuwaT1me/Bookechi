package fuwafuwa.time.bookechi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.DeletedRecord
import fuwafuwa.time.bookechi.data.model.ReadingSession

@Database(
    entities = [Book::class, ReadingSession::class, DeletedRecord::class],
    version = 4,
    exportSchema = false
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun deletedRecordDao(): DeletedRecordDao
}

/** v1 → v2: добавлено поле оценки книги. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Book ADD COLUMN rating INTEGER NOT NULL DEFAULT 0")
    }
}

/** v2 → v3: добавлено поле заметки/цитаты к книге. */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Book ADD COLUMN note TEXT NOT NULL DEFAULT ''")
    }
}

/**
 * v3 → v4: sync-метаданные для синхронизации с Firestore.
 *
 * Существующим строкам раздаём стабильные uuid: книгам — случайные (hex от randomblob),
 * сессиям — детерминированные "${bookUuid}_${date}". Помечаем всё dirty=1, чтобы при
 * первом синке локальные данные уехали в облако. Плюс таблица tombstone'ов для удалений.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()

        // Book: sync-колонки + уникальный индекс по uuid
        db.execSQL("ALTER TABLE Book ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE Book ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE Book ADD COLUMN dirty INTEGER NOT NULL DEFAULT 1")
        db.execSQL("UPDATE Book SET uuid = lower(hex(randomblob(16))), updatedAt = $now")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_Book_uuid ON Book(uuid)")

        // ReadingSession: sync-колонки (bookUuid заполняем из Book, затем детерминированный uuid)
        db.execSQL("ALTER TABLE ReadingSession ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE ReadingSession ADD COLUMN bookUuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE ReadingSession ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE ReadingSession ADD COLUMN dirty INTEGER NOT NULL DEFAULT 1")
        db.execSQL(
            "UPDATE ReadingSession SET bookUuid = " +
                "(SELECT b.uuid FROM Book b WHERE b.id = ReadingSession.bookId)",
        )
        db.execSQL("UPDATE ReadingSession SET uuid = bookUuid || '_' || date, updatedAt = $now")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_ReadingSession_uuid ON ReadingSession(uuid)")

        // Tombstone'ы удалений
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS DeletedRecord (" +
                "uuid TEXT NOT NULL, type TEXT NOT NULL, updatedAt INTEGER NOT NULL, " +
                "dirty INTEGER NOT NULL, PRIMARY KEY(uuid))",
        )
    }
}
