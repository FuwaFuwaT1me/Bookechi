package fuwafuwa.time.bookechi.ui.feature.book_list.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import fuwafuwa.time.bookechi.ui.theme.LocalBottomBarHeight
import fuwafuwa.time.bookechi.ui.theme.LocalThemeToggle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DayDot
import fuwafuwa.time.bookechi.base.ui.ds.DayState
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.EmptyState
import fuwafuwa.time.bookechi.base.ui.ds.InsightPlinth
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.ProgressBar
import fuwafuwa.time.bookechi.base.ui.ds.QuickLogChip
import fuwafuwa.time.bookechi.base.ui.ds.SecondaryButton
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.StatusChip
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListAction
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListState
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.BookListViewModel
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.DayStreak
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.serialization.Serializable
import kotlin.math.ceil

@Serializable
data object BookListScreen : Screen

/** Подписи дней недели Пн..Вс под weekDayStreaks. */
@Composable
private fun weekDayLabels(): List<String> = listOf(
    stringResource(R.string.home_weekday_mon),
    stringResource(R.string.home_weekday_tue),
    stringResource(R.string.home_weekday_wed),
    stringResource(R.string.home_weekday_thu),
    stringResource(R.string.home_weekday_fri),
    stringResource(R.string.home_weekday_sat),
    stringResource(R.string.home_weekday_sun),
)

/** Среднее число страниц в день для прогноза, когда нет точных данных. */
private const val DEFAULT_PAGES_PER_DAY = 25 // TODO: compute from reading sessions average

@Composable
fun BookListScreen(
    viewModel: BookListViewModel,
) {
    val state by viewModel.model.state.collectAsState()

    BookListScreenPrivate(
        state = state,
        onAction = viewModel::sendAction,
    )
}

@Composable
private fun BookListScreenPrivate(
    state: BookListState,
    onAction: (BookListAction) -> Unit,
    onToggleTheme: () -> Unit = {},
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas),
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = colors.accent)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.home_error_generic),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                    )
                }
            }

            else -> BookListContent(
                state = state,
                onAction = onAction,
                onToggleTheme = onToggleTheme,
            )
        }
    }

    if (state.showReminderSheet) {
        ReminderBottomSheet(
            enabled = state.reminderEnabled,
            time = state.reminderTime,
            onToggle = { onAction(BookListAction.SetReminderEnabled(it)) },
            onTimeSelected = { onAction(BookListAction.SetReminderTime(it)) },
            onDismiss = { onAction(BookListAction.CloseReminderSheet) },
        )
    }
}

@Composable
private fun BookListContent(
    state: BookListState,
    onAction: (BookListAction) -> Unit,
    onToggleTheme: () -> Unit,
) {
    val activeBook = state.books.firstOrNull { it.readingStatus == ReadingStatus.Reading }
        ?: state.books.firstOrNull { it.readingStatus == ReadingStatus.None }
    val restBooks = state.books.filter { it != activeBook }
    val plannedBooks = state.books.filter { it.readingStatus == ReadingStatus.Planned }
    val hasBooks = state.books.isNotEmpty()
    val markedToday = state.isTodayStreak || state.pagesReadToday > 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Spacing.lg,
            end = Spacing.lg,
            top = Spacing.lg,
            bottom = LocalBottomBarHeight.current + Spacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        item { HomeHeader(onToggleTheme = onToggleTheme) }

        item {
            StreakCard(
                state = state,
                isComeback = state.isComeback,
                hasBooks = hasBooks,
                markedToday = markedToday,
                onBellClick = { onAction(BookListAction.OpenReminderSheet) },
            )
        }

        when {
            !hasBooks -> {
                item {
                    EmptyState(
                        icon = Icons.AutoMirrored.Outlined.MenuBook,
                        title = stringResource(R.string.home_empty_title),
                        subtitle = stringResource(R.string.home_empty_subtitle),
                        ctaText = stringResource(R.string.home_empty_cta),
                        onCta = { onAction(BookListAction.NavigateToAddBook) },
                    )
                }
            }

            activeBook == null -> {
                item {
                    Text(
                        text = stringResource(R.string.home_what_reading_now),
                        style = MaterialTheme.typography.headlineSmall,
                        color = BookechiTheme.colors.textPrimary,
                    )
                }
                items(items = plannedBooks, key = { it.id }) { book ->
                    BookListRow(
                        book = book,
                        onClick = { onAction(BookListAction.NavigateToBookDetails(book)) },
                        onAction = { onAction(BookListAction.NavigateToEditBook(book)) },
                    )
                }
            }

            else -> {
                item {
                    ActiveBookHeroCard(
                        book = activeBook,
                        markedToday = markedToday,
                        pagesReadToday = state.pagesReadToday,
                        onClick = { onAction(BookListAction.NavigateToBookDetails(activeBook)) },
                        onQuickLog = { onAction(BookListAction.NavigateToEditBook(activeBook)) },
                        onMarkProgress = { onAction(BookListAction.NavigateToEditBook(activeBook)) },
                    )
                }

                if (restBooks.isNotEmpty()) {
                    item { SectionLabel(text = stringResource(R.string.home_more_reading_planned)) }
                    items(items = restBooks, key = { it.id }) { book ->
                        BookListRow(
                            book = book,
                            onClick = { onAction(BookListAction.NavigateToBookDetails(book)) },
                            onAction = { onAction(BookListAction.NavigateToEditBook(book)) },
                        )
                    }
                }
            }
        }
    }
}

/* ----------------------------------------------------------------------------
 * Шапка
 * ------------------------------------------------------------------------- */
@Composable
private fun HomeHeader(onToggleTheme: () -> Unit) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_greeting),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.home_greeting_question),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
            )
        }
        CircleIconButton(
            icon = if (colors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = stringResource(R.string.home_toggle_theme),
            onClick = LocalThemeToggle.current,
        )
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .border(BorderStroke(1.dp, colors.stroke), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.textPrimary,
            modifier = Modifier.size(20.dp),
        )
    }
}

/* ----------------------------------------------------------------------------
 * Стрик-карточка
 * ------------------------------------------------------------------------- */
@Composable
private fun StreakCard(
    state: BookListState,
    isComeback: Boolean,
    hasBooks: Boolean,
    markedToday: Boolean,
    onBellClick: () -> Unit,
) {
    val colors = BookechiTheme.colors

    val title: String
    val subtitle: String
    when {
        isComeback -> {
            title = stringResource(R.string.home_streak_comeback_title)
            subtitle = stringResource(R.string.home_streak_comeback_subtitle)
        }
        !hasBooks -> {
            title = stringResource(R.string.home_streak_start_title)
            subtitle = stringResource(R.string.home_streak_start_subtitle)
        }
        markedToday -> {
            title = pluralStringResource(
                R.plurals.home_streak_days_in_row,
                state.totalDaysWithStreak,
                state.totalDaysWithStreak,
            )
            subtitle = stringResource(R.string.home_streak_marked_subtitle)
        }
        else -> {
            title = pluralStringResource(
                R.plurals.home_streak_days_in_row,
                state.totalDaysWithStreak,
                state.totalDaysWithStreak,
            )
            subtitle = stringResource(R.string.home_streak_continue_subtitle)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(colors.streakGradientStart, colors.streakGradientEnd),
                ),
                shape = DsShapes.card,
            )
            .border(1.dp, colors.streakCurrentDay, DsShapes.card)
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors.surface, DsShapes.tile),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.size(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            CircleIconButton(
                icon = Icons.Outlined.NotificationsNone,
                contentDescription = stringResource(R.string.home_reminders_cd),
                onClick = onBellClick,
                modifier = Modifier.size(36.dp),
            )
        }

        val dayLabels = weekDayLabels()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            state.weekDayStreaks.forEachIndexed { index, day ->
                val dayState = when {
                    isComeback -> DayState.Empty
                    day.isStreakDay -> DayState.Done
                    day.isToday -> DayState.Today
                    else -> DayState.Empty
                }
                DayDot(
                    label = dayLabels.getOrElse(index) { "" },
                    state = dayState,
                )
            }
        }
    }
}

/* ----------------------------------------------------------------------------
 * Hero-карточка активной книги
 * ------------------------------------------------------------------------- */
@Composable
private fun ActiveBookHeroCard(
    book: Book,
    markedToday: Boolean,
    pagesReadToday: Int,
    onClick: () -> Unit,
    onQuickLog: () -> Unit,
    onMarkProgress: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val safePages = book.pages.coerceAtLeast(1)
    val progress = (book.currentPage.toFloat() / safePages).coerceIn(0f, 1f)
    val percent = (progress * 100).toInt()
    val pagesLeft = (book.pages - book.currentPage).coerceAtLeast(0)
    val daysLeft = ceil(pagesLeft.toFloat() / DEFAULT_PAGES_PER_DAY).toInt().coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated, DsShapes.hero)
            .border(BorderStroke(1.dp, colors.stroke), DsShapes.hero)
            .clip(DsShapes.hero)
            .clickable(onClick = onClick)
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        val coverWidth = 96.dp
        val coverHeight = coverWidth * 1.5f // обложка 2:3
        Row {
            BookCover(
                coverPath = book.coverPath,
                title = book.name,
                author = book.author,
                width = coverWidth,
            )
            Spacer(Modifier.size(Spacing.lg))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(coverHeight),
            ) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Выталкиваем «стр. X/Y» + прогресс к нижней стороне обложки.
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = stringResource(R.string.home_page_prefix),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textSecondary,
                    )
                    Text(
                        text = "${book.currentPage}",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.accent,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = " / ${book.pages}",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textSecondary,
                    )
                }
                Spacer(Modifier.height(Spacing.sm))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressBar(progress = progress, modifier = Modifier.weight(1f))
                    Spacer(Modifier.size(Spacing.sm))
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.accentDeep,
                    )
                }
            }
        }

        if (pagesLeft > 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.size(Spacing.xs))
                Text(
                    text = pluralStringResource(
                        R.plurals.home_pace_days_left,
                        daysLeft,
                        daysLeft,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
        }

        // Чипы быстрого лога временно убраны — прогресс отмечается кнопкой ниже.
        // Оставлены в коде на будущее: для возврата раскомментировать.
        // Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        //     QuickLogChip(text = "+ 10", onClick = onQuickLog)
        //     QuickLogChip(text = "+ 25", onClick = onQuickLog)
        //     QuickLogChip(text = "+ 50", onClick = onQuickLog)
        //     QuickLogChip(text = "+", onClick = onQuickLog)
        // }

        if (markedToday) {
            Text(
                text = pluralStringResource(
                    R.plurals.home_marked_today_pages,
                    pagesReadToday,
                    pagesReadToday,
                ),
                style = MaterialTheme.typography.titleSmall,
                color = colors.accentDeep,
            )
            SecondaryButton(text = stringResource(R.string.home_add_more_pages), onClick = onMarkProgress)
        } else {
            InsightPlinth(
                text = stringResource(R.string.home_pages_not_marked),
                backgroundColor = colors.accentSoft,
                icon = Icons.Outlined.Edit,
                iconTint = colors.accentDeep,
            )
            PrimaryButton(text = stringResource(R.string.home_mark_progress), onClick = onMarkProgress)
        }
    }
}

/* ----------------------------------------------------------------------------
 * Строка списка: карточка с обложкой, названием/автором, прогресс/статус
 * и таблеткой-действием («Дальше» для читаемых, «Начать» для остальных).
 * ------------------------------------------------------------------------- */
private val RowCoverWidth = 80.dp

// Палитра приглушённых тонов (тёплые + холодные) для градиента-фона карточек.
// Каждой книге достаётся свой тон детерминированно по названию — как в дизайне,
// где у карточек есть и персиковые, и голубоватые/сероватые оттенки.
// strength — сила подмешивания к фону: у холодных тонов меньше, чтобы они были
// лёгкими, а не «агрессивными».
private data class CardTint(val color: Color, val strength: Float)

private val CardTints = listOf(
    CardTint(Color(0xFFC97A53), 0.40f), // персик
    CardTint(Color(0xFFB5764F), 0.40f), // терракота
    CardTint(Color(0xFFC68A86), 0.40f), // пыльная роза
    CardTint(Color(0xFFB0766E), 0.40f), // глиняно-розовый
    CardTint(Color(0xFFC79A52), 0.38f), // медовый
    CardTint(Color(0xFFBE8090), 0.40f), // приглушённый розовый
    CardTint(Color(0xFF6E8497), 0.20f), // пыльно-голубой — легче
    CardTint(Color(0xFF7C8A6E), 0.22f), // шалфей — легче
)

private fun cardTintFor(title: String): CardTint {
    val index = ((title.hashCode() % CardTints.size) + CardTints.size) % CardTints.size
    return CardTints[index]
}

@Composable
private fun BookListRow(
    book: Book,
    onClick: () -> Unit,
    onAction: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val safePages = book.pages.coerceAtLeast(1)
    val progress = (book.currentPage.toFloat() / safePages).coerceIn(0f, 1f)
    val isReading = book.readingStatus == ReadingStatus.Reading || book.currentPage > 0
    val actionLabel = if (isReading) stringResource(R.string.home_action_continue)
        else stringResource(R.string.home_action_start)
    val coverHeight = RowCoverWidth * 1.5f // обложка 2:3

    // Градиент уникален для каждой книги: свой приглушённый тон (CardTints по
    // названию) слева → surfaceElevated справа. Тон адаптивен к теме (мешается с
    // surfaceElevated). Контур ниже не даёт карточке сливаться с фоном.
    val tint = cardTintFor(book.name)
    val cardBrush = Brush.horizontalGradient(
        0f to lerp(colors.surfaceElevated, tint.color, tint.strength),
        0.55f to colors.surfaceElevated,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = DsShapes.card, clip = false)
            .background(cardBrush, DsShapes.card)
            .border(BorderStroke(1.dp, colors.stroke), DsShapes.card)
            .clip(DsShapes.card)
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BookCover(
            coverPath = book.coverPath,
            title = book.name,
            author = book.author,
            width = RowCoverWidth,
        )
        Spacer(Modifier.size(Spacing.md))
        Column(
            modifier = Modifier
                .weight(1f)
                .height(coverHeight),
        ) {
            Text(
                text = book.name,
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // Прижимаем прогресс/статус к нижней стороне карточки (как у обложки).
            Spacer(Modifier.weight(1f))
            if (isReading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    ProgressBar(
                        progress = progress,
                        modifier = Modifier.weight(1f),
                        height = 6.dp,
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.accentDeep,
                    )
                }
            } else {
                StatusChip(
                    status = pluralStringResource(
                        R.plurals.home_status_with_pages,
                        book.pages,
                        statusLabel(book.readingStatus),
                        book.pages,
                    ),
                )
            }
        }
        Spacer(Modifier.size(Spacing.md))
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.accent)
                .clickable(onClick = onAction)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        )
    }
}

@Composable
private fun statusLabel(status: ReadingStatus): String = when (status) {
    ReadingStatus.Reading -> stringResource(R.string.home_status_reading)
    ReadingStatus.Planned -> stringResource(R.string.home_status_planned)
    ReadingStatus.Completed -> stringResource(R.string.home_status_completed)
    ReadingStatus.Paused -> stringResource(R.string.home_status_paused)
    ReadingStatus.Dropped -> stringResource(R.string.home_status_dropped)
    ReadingStatus.None -> stringResource(R.string.home_status_none)
}

/* ----------------------------------------------------------------------------
 * Превью
 * ------------------------------------------------------------------------- */
private fun mockBook(
    id: Long,
    name: String,
    author: String,
    current: Int,
    pages: Int,
    status: ReadingStatus,
) = Book(
    id = id,
    name = name,
    author = author,
    coverPath = null,
    pages = pages,
    currentPage = current,
    readingStatus = status,
    isFavorite = false,
)

private val mockWeek = listOf(
    DayStreak(isStreakDay = true, isToday = false),
    DayStreak(isStreakDay = true, isToday = false),
    DayStreak(isStreakDay = true, isToday = false),
    DayStreak(isStreakDay = false, isToday = true),
    DayStreak(isStreakDay = false, isToday = false),
    DayStreak(isStreakDay = false, isToday = false),
    DayStreak(isStreakDay = false, isToday = false),
)

private fun mockState(
    books: List<Book>,
    isTodayStreak: Boolean = false,
    pagesReadToday: Int = 0,
    isComeback: Boolean = false,
    days: List<DayStreak> = mockWeek,
) = BookListState(
    books = books,
    isLoading = false,
    error = null,
    gridColumnCount = 1,
    totalDaysWithStreak = if (isComeback) 0 else 12,
    weekDayStreaks = days,
    isTodayStreak = isTodayStreak,
    weeklyPagesRead = 340,
    weeklyPagesTarget = 400,
    pagesReadToday = pagesReadToday,
    isComeback = isComeback,
)

private val mockBooks = listOf(
    mockBook(1, "Норвежский лес", "Харуки Мураками", 168, 320, ReadingStatus.Reading),
    mockBook(2, "Думай медленно… решай быстро", "Даниэль Канеман", 40, 480, ReadingStatus.Reading),
    mockBook(3, "1984", "Джордж Оруэлл", 0, 328, ReadingStatus.Planned),
)

@Preview(name = "Home Main Light", showBackground = true)
@Composable
private fun HomeMainLightPreview() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            BookListScreenPrivate(state = mockState(mockBooks), onAction = {})
        }
    }
}

@Preview(name = "Home Main Dark", showBackground = true)
@Composable
private fun HomeMainDarkPreview() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            BookListScreenPrivate(state = mockState(mockBooks), onAction = {})
        }
    }
}

@Preview(name = "Home Marked Today Light", showBackground = true)
@Composable
private fun HomeMarkedLightPreview() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            BookListScreenPrivate(
                state = mockState(mockBooks, isTodayStreak = true, pagesReadToday = 32),
                onAction = {},
            )
        }
    }
}

@Preview(name = "Home Empty Light", showBackground = true)
@Composable
private fun HomeEmptyLightPreview() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            BookListScreenPrivate(state = mockState(emptyList()), onAction = {})
        }
    }
}

@Preview(name = "Home Comeback Dark", showBackground = true)
@Composable
private fun HomeComebackDarkPreview() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            BookListScreenPrivate(
                state = mockState(
                    mockBooks,
                    isComeback = true,
                    days = List(7) { DayStreak(isStreakDay = false, isToday = it == 3) },
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "Home No Active Light", showBackground = true)
@Composable
private fun HomeNoActiveLightPreview() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            BookListScreenPrivate(
                state = mockState(
                    listOf(
                        mockBook(10, "Сапиенс", "Юваль Ной Харари", 0, 500, ReadingStatus.Planned),
                        mockBook(11, "Чистый код", "Роберт Мартин", 0, 464, ReadingStatus.Planned),
                    ),
                ),
                onAction = {},
            )
        }
    }
}
