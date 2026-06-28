package fuwafuwa.time.bookechi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fuwafuwa.time.bookechi.data.model.DeletedRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: DeletedRecord)

    /** Tombstone'ы, ещё не запушенные в облако. */
    @Query("SELECT * FROM DeletedRecord WHERE dirty = 1")
    suspend fun getDirty(): List<DeletedRecord>

    @Query("UPDATE DeletedRecord SET dirty = 0 WHERE uuid = :uuid")
    suspend fun markSynced(uuid: String)

    /** Сигнал для пуш-петли: число неотправленных удалений. */
    @Query("SELECT COUNT(*) FROM DeletedRecord WHERE dirty = 1")
    fun dirtyCount(): Flow<Int>
}
