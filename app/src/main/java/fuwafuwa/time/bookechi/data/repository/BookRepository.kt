package fuwafuwa.time.bookechi.data.repository

import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookRepository(
    private val bookDao: BookDao
) {

    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun getBookById(id: Long): Book = withContext(Dispatchers.IO) {
        bookDao.getBookById(id)
    }

    suspend fun insertBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.insertBook(book)
    }

    suspend fun insertBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        bookDao.insertBooks(books)
    }

    suspend fun updateBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.deleteBook(book)
    }

    suspend fun deleteBookById(id: Long) = withContext(Dispatchers.IO) {
        bookDao.deleteBookById(id)
    }

    suspend fun deleteAllBooks() = withContext(Dispatchers.IO) {
        bookDao.deleteAllBooks()
    }
}