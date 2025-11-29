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
}
