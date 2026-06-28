package fuwafuwa.time.bookechi.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Entity(indices = [Index(value = ["uuid"], unique = true)])
@Serializable
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val author: String,
    val coverPath: String?,
    val pages: Int,
    val currentPage: Int,
    val readingStatus: ReadingStatus = ReadingStatus.None,
    val isFavorite: Boolean,
    val rating: Int = 0,
    val note: String = "",
    // --- Sync metadata (Firestore) ---
    /** Стабильный глобальный id книги — ключ документа в Firestore (локальный [id] device-specific). */
    val uuid: String = UUID.randomUUID().toString(),
    /** Время последнего изменения, мс. Для last-write-wins при синхронизации. */
    val updatedAt: Long = System.currentTimeMillis(),
    /** Есть незапушенные локальные изменения. */
    val dirty: Boolean = true,
) : Parcelable
