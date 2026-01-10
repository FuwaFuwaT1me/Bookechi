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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import fuwafuwa.time.bookechi.ui.theme.BlueMain

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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF0F9FF),
                        Color(0xFFF8FAFC),
                        Color.White
                    )
                )
            )
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueMain)
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
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😔",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Something went wrong",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BlueMain)
                .clickable { onBack() }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Go Back",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun BookDetailsContent(
    book: Book,
    onAction: (BookDetailsAction) -> Unit
) {
    val progress = if (book.pages > 0) book.currentPage.toFloat() / book.pages else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onAction(BookDetailsAction.NavigateBack) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF64748B)
                )
            }

            Text(
                text = "Book Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )

            IconButton(
                onClick = { /* TODO: More options */ },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color(0xFF64748B)
                )
            }
        }

        // Book Cover with Progress
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            ProgressBookCoverShowcase(
                book = book,
                imageUri = book.coverPath?.toUri(),
                progress = progress,
                circleSize = 280.dp,
                coverHeight = 190.dp,
                coverWidth = 130.dp,
                onAddPageClick = { /* TODO: Add page dialog */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Book Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = book.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.author,
                    fontSize = 16.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Badge
                val (statusColor, statusText) = when (book.readingStatus) {
                    ReadingStatus.Reading -> Color(0xFF22C55E) to "📖 Reading"
                    ReadingStatus.Paused -> Color(0xFFF59E0B) to "⏸️ Paused"
                    ReadingStatus.Stopped -> Color(0xFF6366F1) to "✅ Finished"
                    ReadingStatus.None -> Color(0xFF94A3B8) to "📚 Not Started"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = book.currentPage.toString(),
                        label = "Current",
                        color = BlueMain
                    )
                    StatItem(
                        value = book.pages.toString(),
                        label = "Total",
                        color = Color(0xFF64748B)
                    )
                    StatItem(
                        value = "${(progress * 100).toInt()}%",
                        label = "Progress",
                        color = Color(0xFF22C55E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        ActionButtonsV2(
            readingStatus = book.readingStatus,
            onAction = onAction,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun ActionButtonsV2(
    readingStatus: ReadingStatus,
    onAction: (BookDetailsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (readingStatus) {
        ReadingStatus.None -> {
            ActionButtonV2(
                text = "Start Reading",
                icon = Icons.Default.PlayArrow,
                gradientColors = listOf(BlueMain, Color(0xFF6366F1)),
                onClick = { onAction(BookDetailsAction.StartReading) },
                modifier = modifier
            )
        }

        ReadingStatus.Reading -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButtonV2(
                    text = "Pause",
                    icon = Icons.Default.Pause,
                    gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFEAB308)),
                    onClick = { onAction(BookDetailsAction.PauseReading) },
                    modifier = Modifier.weight(1f)
                )
                ActionButtonV2(
                    text = "Finish",
                    icon = Icons.Default.Stop,
                    gradientColors = listOf(Color(0xFFEF4444), Color(0xFFF87171)),
                    onClick = { onAction(BookDetailsAction.FinishReading) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        ReadingStatus.Paused -> {
            ActionButtonV2(
                text = "Resume Reading",
                icon = Icons.Default.PlayArrow,
                gradientColors = listOf(Color(0xFF22C55E), Color(0xFF4ADE80)),
                onClick = { onAction(BookDetailsAction.ResumeReading) },
                modifier = modifier
            )
        }

        ReadingStatus.Stopped -> {
            ActionButtonV2(
                text = "Read Again",
                icon = Icons.Default.PlayArrow,
                gradientColors = listOf(BlueMain, Color(0xFF6366F1)),
                onClick = { onAction(BookDetailsAction.StartReadingAgain) },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ActionButtonV2(
    text: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(colors = gradientColors)
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp),
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
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
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
                readingStatus = ReadingStatus.Reading
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
                readingStatus = ReadingStatus.Paused
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenV2NotStartedPreview() {
    BookDetailsScreenV2Content(
        state = BookDetailsState(
            book = Book(
                id = 3,
                name = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                coverPath = null,
                pages = 180,
                currentPage = 0,
                readingStatus = ReadingStatus.None
            )
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenV2FinishedPreview() {
    BookDetailsScreenV2Content(
        state = BookDetailsState(
            book = Book(
                id = 4,
                name = "Война и мир",
                author = "Лев Толстой",
                coverPath = null,
                pages = 1225,
                currentPage = 1225,
                readingStatus = ReadingStatus.Stopped
            )
        ),
        onAction = {}
    )
}

