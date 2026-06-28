package fuwafuwa.time.bookechi.data.local

import androidx.room.*
import fuwafuwa.time.bookechi.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    
    @Query("SELECT * FROM Book")
    fun getAllBooks(): Flow<List<Book>>
    
    @Query("SELECT * FROM Book WHERE id = :id")
    suspend fun getBookById(id: Long): Book
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)
    
    @Update
    suspend fun updateBook(book: Book)
    
    @Delete
    suspend fun deleteBook(book: Book)
    
    @Query("DELETE FROM Book WHERE id = :id")
    suspend fun deleteBookById(id: Long)

    @Query("DELETE FROM Book")
    suspend fun deleteAllBooks()

    // --- Sync ---

    @Query("SELECT * FROM Book")
    suspend fun getAllBooksOnce(): List<Book>

    @Query("SELECT * FROM Book WHERE dirty = 1")
    suspend fun getDirtyBooks(): List<Book>

    @Query("SELECT * FROM Book WHERE uuid = :uuid")
    suspend fun getBookByUuid(uuid: String): Book?

    @Query("UPDATE Book SET dirty = 0 WHERE uuid = :uuid")
    suspend fun markBookSynced(uuid: String)

    @Query("DELETE FROM Book WHERE uuid = :uuid")
    suspend fun deleteBookByUuid(uuid: String)
}
