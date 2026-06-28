package fuwafuwa.time.bookechi.data.repository

import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.local.DeletedRecordDao
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.DeletedRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Все записи проходят через этот репозиторий — поэтому здесь же проставляются sync-метки
 * (updatedAt + dirty) и пишутся tombstone'ы на удаление. Так движок синхронизации
 * ([fuwafuwa.time.bookechi.data.sync.SyncManager]) знает, что и когда отправлять в облако.
 *
 * Синк применяет изменения из облака в обход репозитория, напрямую через DAO, чтобы НЕ
 * пометить их снова как dirty (иначе был бы бесконечный пинг-понг).
 */
class BookRepository(
    private val bookDao: BookDao,
    private val deletedDao: DeletedRecordDao,
) {

    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun getBookById(id: Long): Book = withContext(Dispatchers.IO) {
        bookDao.getBookById(id)
    }

    suspend fun insertBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.insertBook(stamp(book))
    }

    suspend fun insertBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        bookDao.insertBooks(books.map { stamp(it) })
    }

    suspend fun updateBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.updateBook(stamp(book))
    }

    suspend fun deleteBook(book: Book) = withContext(Dispatchers.IO) {
        tombstone(book.uuid)
        bookDao.deleteBook(book)
    }

    suspend fun deleteBookById(id: Long) = withContext(Dispatchers.IO) {
        tombstone(bookDao.getBookById(id).uuid)
        bookDao.deleteBookById(id)
    }

    suspend fun deleteAllBooks() = withContext(Dispatchers.IO) {
        bookDao.getAllBooksOnce().forEach { tombstone(it.uuid) }
        bookDao.deleteAllBooks()
    }

    /** Проставляет sync-метки на локальное изменение: свежий uuid (если пуст), время, dirty. */
    private fun stamp(book: Book): Book = book.copy(
        uuid = book.uuid.ifBlank { UUID.randomUUID().toString() },
        updatedAt = System.currentTimeMillis(),
        dirty = true,
    )

    private suspend fun tombstone(uuid: String) {
        if (uuid.isBlank()) return
        deletedDao.upsert(
            DeletedRecord(
                uuid = uuid,
                type = DeletedRecord.TYPE_BOOK,
                updatedAt = System.currentTimeMillis(),
                dirty = true,
            ),
        )
    }
}
