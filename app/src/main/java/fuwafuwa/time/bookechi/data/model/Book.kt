package fuwafuwa.time.bookechi.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Entity
@Serializable
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val author: String,
    val coverPath: String?,
    val pages: Int,
    val currentPage: Int,
    val readingStatus: ReadingStatus = ReadingStatus.None
) : Parcelable
