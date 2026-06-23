package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class ShelfModel(
    defaultState: ShelfState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<ShelfState, ShelfAction>(defaultState) {

    init {
        scope.launch {
            combine(
                bookRepository.getAllBooks(),
                readingSessionRepository.getAllSessions(),
            ) { books, sessions -> books to sessions }
                .collect { (books, sessions) ->
                    val completed = books.filter { it.readingStatus == ReadingStatus.Completed }
                    val byBook = sessions.groupBy { it.bookId }

                    val items = completed.map { b ->
                        val dates = byBook[b.id].orEmpty().mapNotNull {
                            runCatching { LocalDate.parse(it.date) }.getOrNull()
                        }
                        ShelfBookItem(
                            bookId = b.id,
                            title = b.name,
                            author = b.author,
                            coverPath = b.coverPath,
                            favorite = b.isFavorite,
                            finishedAt = dates.maxOrNull(),
                        )
                    }

                    val months = items
                        .groupBy { it.finishedAt?.let { d -> d.year * 100 + d.monthValue } ?: 0 }
                        .entries
                        .sortedByDescending { it.key }
                        .map { (key, list) ->
                            ShelfMonth(
                                year = if (key == 0) 0 else key / 100,
                                monthValue = if (key == 0) 0 else key % 100,
                                books = list.sortedByDescending { it.finishedAt ?: LocalDate.MIN },
                            )
                        }

                    updateState {
                        copy(months = months, totalCount = completed.size, isLoading = false)
                    }
                }
        }
    }

    override fun onAction(action: ShelfAction) {
        when (action) {
            ShelfAction.NavigateBack -> sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            is ShelfAction.OpenBook -> sendNavigationEvent(NavigateToShelfBook(action.bookId))
        }
    }
}
