package fuwafuwa.time.bookechi.data.sync

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import fuwafuwa.time.bookechi.data.auth.AuthRepository
import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.local.DeletedRecordDao
import fuwafuwa.time.bookechi.data.local.ReadingSessionDao
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.DeletedRecord
import fuwafuwa.time.bookechi.data.model.ReadingSession
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

/**
 * Движок синхронизации Room ↔ Firestore (local-first: Room — источник правды, Firestore — слой синка).
 *
 * Модель: `users/{uid}/books/{bookUuid}` и `users/{uid}/sessions/{sessionUuid}`.
 * - **Push**: наблюдаем за локальными изменениями (dirty-строки + tombstone'ы), с дебаунсом
 *   отправляем их в облако. Конфликты — last-write-wins по `updatedAt`.
 * - **Pull**: realtime-слушатель Firestore применяет изменения из облака напрямую в DAO
 *   (в обход репозиториев, чтобы не пометить их снова dirty). Эхо собственных записей
 *   отсекается проверкой `local.updatedAt >= remote.updatedAt`.
 *
 * Запускается один раз на процесс ([start]); слушатели переключаются при смене аккаунта.
 */
@OptIn(FlowPreview::class)
class SyncManager(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val bookDao: BookDao,
    private val sessionDao: ReadingSessionDao,
    private val deletedDao: DeletedRecordDao,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val pushMutex = Mutex()
    private val listeners = mutableListOf<ListenerRegistration>()
    private var currentUid: String? = null

    fun start() {
        // Переподключаемся при смене аккаунта (анонимный → Google и т.п.)
        scope.launch {
            authRepository.authUser.collect { user -> bind(user?.uid) }
        }
        // Пуш-петля: любое локальное изменение (книги/сессии/удаления) → дебаунс → push
        scope.launch {
            combine(
                bookDao.getAllBooks(),
                sessionDao.getAllSessions(),
                deletedDao.dirtyCount(),
            ) { _, _, _ -> }
                .debounce(PUSH_DEBOUNCE_MS)
                .collect { pushDirty() }
        }
    }

    private fun bind(uid: String?) {
        if (uid == currentUid) return
        listeners.forEach { it.remove() }
        listeners.clear()
        currentUid = uid
        if (uid == null) return

        // Сразу отправим всё, что накопилось локально до входа (или под прошлым аккаунтом).
        scope.launch { pushDirty() }

        // Realtime-слушатели на изменения в облаке.
        listeners += booksCol(uid).addSnapshotListener { snap, e ->
            if (e != null) { Log.w(TAG, "books listen failed", e); return@addSnapshotListener }
            snap ?: return@addSnapshotListener
            scope.launch { applyRemoteBooks(snap.documents) }
        }
        listeners += sessionsCol(uid).addSnapshotListener { snap, e ->
            if (e != null) { Log.w(TAG, "sessions listen failed", e); return@addSnapshotListener }
            snap ?: return@addSnapshotListener
            scope.launch { applyRemoteSessions(snap.documents) }
        }
    }

    // --- Push ---------------------------------------------------------------

    private suspend fun pushDirty(): Unit = pushMutex.withLock {
        val uid = currentUid ?: return
        runCatching {
            bookDao.getDirtyBooks().forEach { book ->
                booksCol(uid).document(book.uuid).set(book.toMap(), SetOptions.merge()).await()
                bookDao.markBookSynced(book.uuid)
            }
            sessionDao.getDirtySessions().forEach { session ->
                sessionsCol(uid).document(session.uuid).set(session.toMap(), SetOptions.merge()).await()
                sessionDao.markSessionSynced(session.uuid)
            }
            deletedDao.getDirty().forEach { tomb ->
                collectionFor(uid, tomb.type)
                    .document(tomb.uuid)
                    .set(deletedMarker(tomb.updatedAt), SetOptions.merge())
                    .await()
                deletedDao.markSynced(tomb.uuid)
            }
        }.onFailure { Log.w(TAG, "pushDirty failed", it) }
        Unit
    }

    // --- Pull ---------------------------------------------------------------

    private suspend fun applyRemoteBooks(docs: List<DocumentSnapshot>) {
        for (doc in docs) {
            runCatching {
                val uuid = doc.id
                if (doc.getBoolean(FIELD_DELETED) == true) {
                    bookDao.deleteBookByUuid(uuid) // каскад удалит и сессии книги
                    return@runCatching
                }
                val remoteUpdatedAt = doc.getLong("updatedAt") ?: 0L
                val local = bookDao.getBookByUuid(uuid)
                if (local != null && local.updatedAt >= remoteUpdatedAt) return@runCatching // локальное свежее/такое же

                val merged = doc.toBook(
                    id = local?.id ?: 0L,
                    // coverPath — локальный путь к файлу, в облаке его нет: сохраняем имеющийся (фаза 3).
                    coverPath = local?.coverPath,
                )
                if (local != null) bookDao.updateBook(merged) else bookDao.insertBook(merged)
            }.onFailure { Log.w(TAG, "applyRemoteBooks doc=${doc.id} failed", it) }
        }
    }

    private suspend fun applyRemoteSessions(docs: List<DocumentSnapshot>) {
        for (doc in docs) {
            runCatching {
                val uuid = doc.id
                if (doc.getBoolean(FIELD_DELETED) == true) {
                    sessionDao.deleteSessionByUuid(uuid)
                    return@runCatching
                }
                val bookUuid = doc.getString("bookUuid") ?: return@runCatching
                // Резолвим родительскую книгу: локальный bookId device-specific.
                val book = bookDao.getBookByUuid(bookUuid) ?: return@runCatching // книги ещё/уже нет — пропускаем
                val remoteUpdatedAt = doc.getLong("updatedAt") ?: 0L
                val local = sessionDao.getSessionByUuid(uuid)
                if (local != null && local.updatedAt >= remoteUpdatedAt) return@runCatching

                val merged = doc.toSession(id = local?.id ?: 0L, bookId = book.id)
                if (local != null) sessionDao.updateSession(merged) else sessionDao.insertSession(merged)
            }.onFailure { Log.w(TAG, "applyRemoteSessions doc=${doc.id} failed", it) }
        }
    }

    // --- Firestore helpers --------------------------------------------------

    private fun userDoc(uid: String) = firestore.collection("users").document(uid)
    private fun booksCol(uid: String) = userDoc(uid).collection("books")
    private fun sessionsCol(uid: String) = userDoc(uid).collection("sessions")

    private fun collectionFor(uid: String, type: String): CollectionReference =
        if (type == DeletedRecord.TYPE_BOOK) booksCol(uid) else sessionsCol(uid)

    private fun deletedMarker(updatedAt: Long) = mapOf(
        FIELD_DELETED to true,
        "updatedAt" to updatedAt,
    )

    companion object {
        private const val TAG = "SyncManager"
        private const val PUSH_DEBOUNCE_MS = 1500L
        private const val FIELD_DELETED = "deleted"
    }
}

// --- Mapping: entity ↔ Firestore document -----------------------------------

private fun Book.toMap(): Map<String, Any?> = mapOf(
    "uuid" to uuid,
    "name" to name,
    "author" to author,
    "pages" to pages,
    "currentPage" to currentPage,
    "readingStatus" to readingStatus.name,
    "isFavorite" to isFavorite,
    "rating" to rating,
    "note" to note,
    "updatedAt" to updatedAt,
    "deleted" to false,
)

private fun DocumentSnapshot.toBook(id: Long, coverPath: String?): Book = Book(
    id = id,
    name = getString("name").orEmpty(),
    author = getString("author").orEmpty(),
    coverPath = coverPath,
    pages = (getLong("pages") ?: 0L).toInt(),
    currentPage = (getLong("currentPage") ?: 0L).toInt(),
    readingStatus = runCatching { ReadingStatus.valueOf(getString("readingStatus") ?: "None") }
        .getOrDefault(ReadingStatus.None),
    isFavorite = getBoolean("isFavorite") ?: false,
    rating = (getLong("rating") ?: 0L).toInt(),
    note = getString("note").orEmpty(),
    uuid = this.id, // id документа == uuid книги
    updatedAt = getLong("updatedAt") ?: 0L,
    dirty = false, // пришло из облака — синхронизировано
)

private fun ReadingSession.toMap(): Map<String, Any?> = mapOf(
    "uuid" to uuid,
    "bookUuid" to bookUuid,
    "date" to date,
    "pagesRead" to pagesRead,
    "readingTimeMinutes" to readingTimeMinutes,
    "startPage" to startPage,
    "endPage" to endPage,
    "updatedAt" to updatedAt,
    "deleted" to false,
)

private fun DocumentSnapshot.toSession(id: Long, bookId: Long): ReadingSession = ReadingSession(
    id = id,
    bookId = bookId,
    date = getString("date").orEmpty(),
    pagesRead = (getLong("pagesRead") ?: 0L).toInt(),
    readingTimeMinutes = (getLong("readingTimeMinutes") ?: 0L).toInt(),
    startPage = (getLong("startPage") ?: 0L).toInt(),
    endPage = (getLong("endPage") ?: 0L).toInt(),
    uuid = this.id,
    bookUuid = getString("bookUuid").orEmpty(),
    updatedAt = getLong("updatedAt") ?: 0L,
    dirty = false,
)
