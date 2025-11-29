package fuwafuwa.time.bookechi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val author: String,
    val coverPath: String,
    val pages: Int,
    val currentPage: Int
)
