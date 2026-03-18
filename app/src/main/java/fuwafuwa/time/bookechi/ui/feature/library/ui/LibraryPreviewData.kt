package fuwafuwa.time.bookechi.ui.feature.library.ui

import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus

internal object LibraryPreviewData {
    fun books(): List<Book> = listOf(
        Book(
            id = 1,
            name = "Если все кошки в мире исчезнут",
            author = "Гэнки Кавамура",
            coverPath = null,
            pages = 320,
            currentPage = 54,
            readingStatus = ReadingStatus.Reading,
            isFavorite = false,
        ),
        Book(
            id = 2,
            name = "Песнь безродного",
            author = "Карина Володина",
            coverPath = null,
            pages = 480,
            currentPage = 480,
            readingStatus = ReadingStatus.Completed,
            isFavorite = false,
        ),
        Book(
            id = 3,
            name = "Туманы Авалона. Том 1",
            author = "Мэрион Зиммер Брэдли",
            coverPath = null,
            pages = 640,
            currentPage = 0,
            readingStatus = ReadingStatus.None,
            isFavorite = false,
        )
    )
}
