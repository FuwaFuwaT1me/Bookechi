package fuwafuwa.time.bookechi.ui.feature.book_details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.ProgressBookCoverShowcase
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsAction
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsState
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsViewModel
import fuwafuwa.time.bookechi.ui.theme.FigmaFire
import fuwafuwa.time.bookechi.ui.theme.FigmaLibraryBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
fun BookDetailsScreenV2(
    viewModel: BookDetailsViewModel
) {
    val state by viewModel.model.state.collectAsState()

    BookDetailsScreenV2Content(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun BookDetailsScreenV2Content(
    state: BookDetailsState,
    onAction: (BookDetailsAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaLibraryBackground)
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FigmaTitle)
                }
            }

            state.error != null -> {
                ErrorContent(onBack = { onAction(BookDetailsAction.NavigateBack) })
            }

            state.book != null -> {
                BookDetailsContent(
                    book = state.book,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Не удалось загрузить книгу",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = FigmaTitle,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Попробуйте вернуться назад",
            fontSize = 14.sp,
            color = FigmaSubtitle
        )
        Spacer(modifier = Modifier.height(20.dp))
        ActionButtonV2(
            text = "Назад",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            backgroundColor = FigmaTitle,
            onClick = onBack
        )
    }
}

@Composable
private fun BookDetailsContent(
    book: Book,
    onAction: (BookDetailsAction) -> Unit
) {
    val progress = if (book.pages > 0) {
        (book.currentPage.toFloat() / book.pages).coerceIn(0f, 1f)
    } else {
        0f
    }
    val statusUi = readingStatusUi(book.readingStatus)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        BookDetailsHeader(
            onBackClick = { onAction(BookDetailsAction.NavigateBack) },
            onMoreClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressBookCoverShowcase(
                    book = book,
                    imageUri = book.coverPath?.toUri(),
                    progress = progress,
                    circleSize = 250.dp,
                    coverHeight = 170.dp,
                    coverWidth = 120.dp,
                    onAddPageClick = { /* TODO */ },
                    accentColor = FigmaFire
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = book.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = FigmaTitle,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = book.author,
                    fontSize = 15.sp,
                    color = FigmaSubtitle,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusUi.color.copy(alpha = 0.14f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusUi.text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusUi.color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = book.currentPage.toString(),
                    label = "Текущая",
                    color = FigmaTitle
                )
                StatItem(
                    value = book.pages.toString(),
                    label = "Всего",
                    color = FigmaSubtitle
                )
                StatItem(
                    value = "${(progress * 100).toInt()}%",
                    label = "Прогресс",
                    color = FigmaFire
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ActionButtonsV2(
            readingStatus = book.readingStatus,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun BookDetailsHeader(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Назад",
            onClick = onBackClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Детали книги",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = FigmaTitle
        )

        Spacer(modifier = Modifier.weight(1f))

        HeaderIconButton(
            icon = Icons.Default.MoreVert,
            contentDescription = "Дополнительно",
            onClick = onMoreClick
        )
    }
}

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFEFE4DE))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = FigmaTitle
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = FigmaSubtitle
        )
    }
}

@Composable
private fun ActionButtonsV2(
    readingStatus: ReadingStatus,
    onAction: (BookDetailsAction) -> Unit
) {
    when (readingStatus) {
        ReadingStatus.None,
        ReadingStatus.Planned -> {
            ActionButtonV2(
                text = "Начать читать",
                icon = Icons.Default.PlayArrow,
                backgroundColor = FigmaTitle,
                onClick = { onAction(BookDetailsAction.StartReading) }
            )
        }

        ReadingStatus.Reading -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButtonV2(
                    text = "Пауза",
                    icon = Icons.Default.Pause,
                    backgroundColor = Color(0xFFB98A63),
                    onClick = { onAction(BookDetailsAction.PauseReading) },
                    modifier = Modifier.weight(1f)
                )
                ActionButtonV2(
                    text = "Завершить",
                    icon = Icons.Default.Stop,
                    backgroundColor = FigmaFire,
                    onClick = { onAction(BookDetailsAction.FinishReading) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        ReadingStatus.Paused -> {
            ActionButtonV2(
                text = "Продолжить",
                icon = Icons.Default.PlayArrow,
                backgroundColor = FigmaTitle,
                onClick = { onAction(BookDetailsAction.ResumeReading) }
            )
        }

        ReadingStatus.Dropped,
        ReadingStatus.Completed -> {
            ActionButtonV2(
                text = "Читать снова",
                icon = Icons.Default.PlayArrow,
                backgroundColor = FigmaTitle,
                onClick = { onAction(BookDetailsAction.StartReadingAgain) }
            )
        }
    }
}

@Composable
private fun ActionButtonV2(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private data class ReadingStatusUi(
    val text: String,
    val color: Color
)

private fun readingStatusUi(status: ReadingStatus): ReadingStatusUi = when (status) {
    ReadingStatus.None -> ReadingStatusUi("Не начата", FigmaSubtitle)
    ReadingStatus.Planned -> ReadingStatusUi("В планах", FigmaSubtitle)
    ReadingStatus.Reading -> ReadingStatusUi("Читаю", FigmaFire)
    ReadingStatus.Paused -> ReadingStatusUi("На паузе", Color(0xFFB98A63))
    ReadingStatus.Dropped -> ReadingStatusUi("Брошена", Color(0xFF9A7E72))
    ReadingStatus.Completed -> ReadingStatusUi("Прочитана", FigmaTitle)
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenV2Preview() {
    BookDetailsScreenV2Content(
        state = BookDetailsState(
            book = Book(
                id = 1,
                name = "Хроники заводной птицы",
                author = "Харуки Мураками",
                coverPath = null,
                pages = 1052,
                currentPage = 448,
                readingStatus = ReadingStatus.Reading,
                isFavorite = false
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenV2PausedPreview() {
    BookDetailsScreenV2Content(
        state = BookDetailsState(
            book = Book(
                id = 2,
                name = "1984",
                author = "George Orwell",
                coverPath = null,
                pages = 328,
                currentPage = 150,
                readingStatus = ReadingStatus.Paused,
                isFavorite = false
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenV2PlannedPreview() {
    BookDetailsScreenV2Content(
        state = BookDetailsState(
            book = Book(
                id = 3,
                name = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                coverPath = null,
                pages = 180,
                currentPage = 0,
                readingStatus = ReadingStatus.Planned,
                isFavorite = false
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenV2DroppedPreview() {
    BookDetailsScreenV2Content(
        state = BookDetailsState(
            book = Book(
                id = 4,
                name = "Война и мир",
                author = "Лев Толстой",
                coverPath = null,
                pages = 1225,
                currentPage = 1225,
                readingStatus = ReadingStatus.Dropped,
                isFavorite = false
            )
        ),
        onAction = {}
    )
}
