package fuwafuwa.time.bookechi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tombstone (надгробие) удалённой записи. Локально мы удаляем строку физически
 * (запросы остаются простыми, без фильтра isDeleted), а факт удаления держим тут,
 * чтобы синк проставил `deleted = true` соответствующему документу в Firestore —
 * и удаление доехало до других устройств.
 */
@Entity
data class DeletedRecord(
    /** uuid удалённой сущности (книги или сессии). */
    @PrimaryKey val uuid: String,
    /** Тип записи: [TYPE_BOOK] или [TYPE_SESSION]. */
    val type: String,
    val updatedAt: Long,
    /** Удаление ещё не запушено в облако. */
    val dirty: Boolean,
) {
    companion object {
        const val TYPE_BOOK = "book"
        const val TYPE_SESSION = "session"
    }
}
