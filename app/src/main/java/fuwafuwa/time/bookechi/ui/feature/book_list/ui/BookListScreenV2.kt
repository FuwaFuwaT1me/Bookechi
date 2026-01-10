package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.BookCover
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.data.preferences.BookListViewType
import fuwafuwa.time.bookechi.data.preferences.DesignPreferences
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListAction
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import org.koin.compose.koinInject

@Composable
fun BookListScreenV2(
    viewModel: BookListViewModel,
    appPreferences: AppPreferences = koinInject()
) {
    val state by viewModel.model.state.collectAsState()
    val designPrefs by appPreferences.designPreferences.collectAsState()

    BookListScreenV2Content(
        state = state,
        designPrefs = designPrefs,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun BookListScreenV2Content(
    state: BookListState,
    designPrefs: DesignPreferences,
    onAction: (BookListAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📚 My Library",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${state.books.size} books in collection",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
                
                // View type indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (designPrefs.bookListViewType == BookListViewType.GRID) 
                                Icons.Default.GridView else Icons.Default.ViewList,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        if (designPrefs.bookListViewType == BookListViewType.GRID) {
                            Text(
                                text = "${designPrefs.gridColumns}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

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
                    ErrorCard()
                }

                state.books.isEmpty() -> {
                    EmptyBookListV2(
                        onAddBookClick = { onAction(BookListAction.NavigateToAddBook) }
                    )
                }

                else -> {
                    when (designPrefs.bookListViewType) {
                        BookListViewType.LIST -> {
                            BooksListView(
                                books = state.books,
                                onAction = onAction
                            )
                        }
                        BookListViewType.GRID -> {
                            BooksGridView(
                                books = state.books,
                                columns = designPrefs.gridColumns,
                                onAction = onAction
                            )
                        }
                    }
                }
            }
        }

        // FAB
        if (state.books.isNotEmpty()) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                onClick = { onAction(BookListAction.NavigateToAddBook) },
                containerColor = BlueMain,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add book",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun ErrorCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
    ) {
        Text(
            text = "😔 Something went wrong. Please try again.",
            modifier = Modifier.padding(20.dp),
            color = Color(0xFFDC2626),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun EmptyBookListV2(
    onAddBookClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📖",
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your library is empty",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add your first book to start\ntracking your reading journey",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(BlueMain, Color(0xFF6366F1))
                            )
                        )
                        .clickable { onAddBookClick() }
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Add First Book",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// LIST VIEW
@Composable
private fun BooksListView(
    books: List<Book>,
    onAction: (BookListAction) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookItemListView(
                book = book,
                onClick = { onAction(BookListAction.NavigateToBookDetails(book)) },
                onDeleteClick = { onAction(BookListAction.DeleteBook(book)) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun BookItemListView(
    book: Book,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val progress = if (book.pages > 0) book.currentPage.toFloat() / book.pages else 0f
    val progressPercent = (progress * 100).toInt()

    val statusColor = when (book.readingStatus) {
        ReadingStatus.Reading -> Color(0xFF22C55E)
        ReadingStatus.Paused -> Color(0xFFF59E0B)
        ReadingStatus.Stopped -> Color(0xFF6366F1)
        ReadingStatus.None -> Color(0xFF94A3B8)
    }

    val statusText = when (book.readingStatus) {
        ReadingStatus.Reading -> "Reading"
        ReadingStatus.Paused -> "Paused"
        ReadingStatus.Stopped -> "Finished"
        ReadingStatus.None -> "Not started"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Cover
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 85.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                BookCover(
                    imageUri = book.coverPath?.toUri(),
                    onClick = onClick
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Book Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = book.author,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BlueMain,
                        trackColor = Color(0xFFE2E8F0),
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = "$progressPercent%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }

                Text(
                    text = "${book.currentPage} / ${book.pages} pages",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // More Button
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color(0xFF94A3B8)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Delete",
                                    color = Color(0xFFEF4444)
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}

// GRID VIEW
@Composable
private fun BooksGridView(
    books: List<Book>,
    columns: Int,
    onAction: (BookListAction) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookItemGridView(
                book = book,
                onClick = { onAction(BookListAction.NavigateToBookDetails(book)) },
                onDeleteClick = { onAction(BookListAction.DeleteBook(book)) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun BookItemGridView(
    book: Book,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val progress = if (book.pages > 0) book.currentPage.toFloat() / book.pages else 0f
    val progressPercent = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cover with menu
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    BookCover(
                        imageUri = book.coverPath?.toUri(),
                        onClick = onClick,
                        onLongTap = { showMenu = true }
                    )
                }
                
                // Progress badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(BlueMain.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$progressPercent%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Delete",
                                    color = Color(0xFFEF4444)
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = book.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Author
            Text(
                text = book.author,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = BlueMain,
                trackColor = Color(0xFFE2E8F0),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookListScreenV2ListPreview() {
    BookListScreenV2Content(
        state = BookListState(
            books = sampleBooks
        ),
        designPrefs = DesignPreferences(
            bookListViewType = BookListViewType.LIST
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookListScreenV2GridPreview() {
    BookListScreenV2Content(
        state = BookListState(
            books = sampleBooks
        ),
        designPrefs = DesignPreferences(
            bookListViewType = BookListViewType.GRID,
            gridColumns = 3
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookListScreenV2EmptyPreview() {
    BookListScreenV2Content(
        state = BookListState(books = emptyList()),
        designPrefs = DesignPreferences(),
        onAction = {}
    )
}

private val sampleBooks = listOf(
    Book(
        id = 1,
        name = "The Great Gatsby",
        author = "F. Scott Fitzgerald",
        coverPath = null,
        currentPage = 120,
        pages = 180,
        readingStatus = ReadingStatus.Reading
    ),
    Book(
        id = 2,
        name = "1984",
        author = "George Orwell",
        coverPath = null,
        currentPage = 328,
        pages = 328,
        readingStatus = ReadingStatus.Stopped
    ),
    Book(
        id = 3,
        name = "Хроники заводной птицы",
        author = "Харуки Мураками",
        coverPath = null,
        currentPage = 200,
        pages = 1052,
        readingStatus = ReadingStatus.Paused
    ),
    Book(
        id = 4,
        name = "Война и мир",
        author = "Лев Толстой",
        coverPath = null,
        currentPage = 0,
        pages = 1225,
        readingStatus = ReadingStatus.None
    )
)
