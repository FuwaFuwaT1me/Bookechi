package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.NavigateToBookDetails
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ShelfBookModel(
    defaultState: ShelfBookState,
    private val bookRepository: BookRepository,
    private val readingSessionRepository: ReadingSessionRepository,
) : BaseModel<ShelfBookState, ShelfBookAction>(defaultState) {

    private val bookId = defaultState.bookId

    init {
        scope.launch {
            combine(
                bookRepository.getAllBooks(),
                readingSessionRepository.getSessionsForBook(bookId),
            ) { books, sessions -> books to sessions }
                .collect { (books, sessions) ->
                    val book = books.firstOrNull { it.id == bookId }
                    val sorted = sessions
                        .mapNotNull { s -> runCatching { LocalDate.parse(s.date) }.getOrNull()?.let { it to s } }
                        .sortedBy { it.first }
                    val pages = (book?.pages ?: 0).coerceAtLeast(1)
                    val started = sorted.firstOrNull()?.first
                    val finished = sorted.lastOrNull()?.first
                    val days = if (started != null && finished != null) {
                        ChronoUnit.DAYS.between(started, finished).toInt() + 1
                    } else {
                        0
                    }
                    updateState {
                        copy(
                            title = book?.name ?: "",
                            author = book?.author ?: "",
                            coverPath = book?.coverPath,
                            favorite = book?.isFavorite ?: false,
                            pages = book?.pages ?: 0,
                            sessionsCount = sorted.size,
                            totalMinutes = sorted.sumOf { it.second.readingTimeMinutes },
                            startedAt = started,
                            finishedAt = finished,
                            daysCount = days,
                            progress = sorted.map { (_, s) -> (s.endPage.toFloat() / pages).coerceIn(0f, 1f) },
                            recordPages = sorted.maxOfOrNull { it.second.pagesRead } ?: 0,
                            rating = book?.rating ?: 0,
                            book = book,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    override fun onAction(action: ShelfBookAction) {
        when (action) {
            ShelfBookAction.NavigateBack -> sendNavigationEvent(BaseNavigationEvent.NavigateBack)
            ShelfBookAction.OpenBookPage -> {
                val book = state.value.book ?: return
                sendNavigationEvent(NavigateToBookDetails(book))
            }
            ShelfBookAction.ToggleFavorite -> {
                val book = state.value.book ?: return
                scope.launch { bookRepository.updateBook(book.copy(isFavorite = !book.isFavorite)) }
            }
            ShelfBookAction.OpenRatingSheet -> updateState { copy(isRatingSheetOpen = true) }
            ShelfBookAction.CloseRatingSheet -> updateState { copy(isRatingSheetOpen = false) }
            is ShelfBookAction.SetRating -> {
                val book = state.value.book ?: return
                scope.launch { bookRepository.updateBook(book.copy(rating = action.rating)) }
                updateState { copy(isRatingSheetOpen = false) }
            }
        }
    }
}
