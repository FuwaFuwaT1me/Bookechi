package fuwafuwa.time.bookechi.ui.feature.reading_log.ui

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.util.AnimatedPeriodSwitcher
import fuwafuwa.time.bookechi.base.ui.util.AnimatedPeriodSwitcherConfig
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.PeriodSummary
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.ReadingLogAction
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.ReadingLogState
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.ReadingLogViewModel
import fuwafuwa.time.bookechi.ui.feature.reading_log.mvi.SessionLogItem
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data class ReadingLogScreen(
    val bookId: Long = -1L,
) : Screen

@Composable
fun ReadingLogScreen(viewModel: ReadingLogViewModel) {
    val state by viewModel.model.state.collectAsState()
    ReadingLogContent(state = state, onAction = viewModel::sendAction)
}

@Composable
private fun ReadingLogContent(
    state: ReadingLogState,
    onAction: (ReadingLogAction) -> Unit,
) {
    val colors = BookechiTheme.colors
    var period by remember { mutableIntStateOf(0) }
    var hintOpen by remember { mutableStateOf(true) }
    var toastVisible by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<SessionLogItem?>(null) }
    var deleteItem by remember { mutableStateOf<SessionLogItem?>(null) }

    LaunchedEffect(toastVisible) {
        if (toastVisible) {
            delay(2600)
            toastVisible = false
        }
    }

    val groups = remember(state.items) { groupByBucket(state.items) }

    Box(modifier = Modifier.fillMaxSize().background(colors.canvas)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            LogHeader(
                isBookScope = state.isBookScope,
                title = if (state.isBookScope) state.bookTitle else stringResource(R.string.log_title),
                showHelp = !state.isBookScope && !hintOpen,
                onBack = { onAction(ReadingLogAction.NavigateBack) },
                onHelp = { hintOpen = true },
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(
                    start = Spacing.xl,
                    end = Spacing.xl,
                    bottom = Spacing.xxxl,
                ),
            ) {
                if (!state.isBookScope) {
                    item {
                        Spacer(Modifier.height(Spacing.xs))
                        PeriodSummarySection(
                            state = state,
                            selected = period,
                            onSelect = { period = it },
                        )
                    }
                    if (hintOpen) {
                        item {
                            Spacer(Modifier.height(Spacing.md))
                            HintCard(onClose = {
                                hintOpen = false
                                toastVisible = true
                            })
                        }
                    }
                }

                if (state.items.isEmpty() && !state.isLoading) {
                    item { EmptyLog(isBookScope = state.isBookScope) }
                }

                groups.forEach { (bucket, items) ->
                    item(key = "h$bucket") {
                        DayGroupHeader(bucket = bucket, items = items)
                    }
                    items(items = items, key = { it.sessionId }) { item ->
                        SessionRow(
                            item = item,
                            showCover = !state.isBookScope,
                            onTap = { if (item.isCurrent) editItem = item },
                            onDelete = { deleteItem = item },
                        )
                        Spacer(Modifier.height(Spacing.sm))
                    }
                }
            }
        }

        LogToast(
            visible = toastVisible,
            modifier = Modifier.align(Alignment.BottomCenter).padding(Spacing.xl),
        )
    }

    editItem?.let { item ->
        SessionEditSheet(
            item = item,
            onDismiss = { editItem = null },
            onSave = { to, mins ->
                onAction(ReadingLogAction.SaveSessionEdit(item.sessionId, to, mins))
                editItem = null
            },
            onDelete = {
                editItem = null
                deleteItem = item
            },
        )
    }

    deleteItem?.let { item ->
        SessionDeleteDialog(
            item = item,
            onKeep = { deleteItem = null },
            onConfirm = {
                onAction(ReadingLogAction.DeleteSession(item.sessionId))
                deleteItem = null
            },
        )
    }
}

/* ----- Header ----- */

@Composable
private fun LogHeader(
    isBookScope: Boolean,
    title: String,
    showHelp: Boolean,
    onBack: () -> Unit,
    onHelp: () -> Unit,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.md, bottom = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        CircleIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    if (isBookScope) R.string.log_crumb_book else R.string.log_crumb_productivity
                ),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (showHelp) {
            CircleIconButton(icon = Icons.Filled.HelpOutline, onClick = onHelp)
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val colors = BookechiTheme.colors
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .border(1.dp, colors.stroke, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(18.dp))
    }
}

/* ----- Period summary ----- */

@Composable
private fun PeriodSummarySection(
    state: ReadingLogState,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val colors = BookechiTheme.colors
    val summary = when (selected) {
        1 -> state.monthSummary
        2 -> state.allTimeSummary
        else -> state.weekSummary
    }
    AnimatedPeriodSwitcher(
        values = listOf(
            stringResource(R.string.log_period_week),
            stringResource(R.string.log_period_month),
            stringResource(R.string.log_period_all),
        ),
        modifier = Modifier.fillMaxWidth().height(46.dp),
        selectedIndex = selected,
        innerCornerRadius = 14.dp,
        outerCornerRadius = 16.dp,
        onSwitch = onSelect,
        config = AnimatedPeriodSwitcherConfig(
            containerColor = colors.chipBg,
            selectedColor = colors.surfaceElevated,
            activeTextColor = colors.accentDeep,
            inactiveTextColor = colors.textSecondary,
        ),
    )
    Spacer(Modifier.height(Spacing.md))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.cardTint)
            .padding(horizontal = Spacing.sm, vertical = Spacing.md),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SummaryCell(value = summary.sessions.toString(), label = stringResource(R.string.log_summary_sessions))
            SummaryDivider()
            SummaryCell(value = formatThousands(summary.pages), label = stringResource(R.string.log_summary_pages), accent = true)
            SummaryDivider()
            SummaryCell(value = timeText(summary.minutes), label = stringResource(R.string.log_summary_time))
        }
        // Дельту показываем только если она положительная; «всё время» — всегда (метка книг).
        val showPositiveDelta = selected != 2 && (summary.deltaPercent ?: 0) > 0
        if (selected == 2 || showPositiveDelta) {
            Spacer(Modifier.height(Spacing.md))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.divider))
            Spacer(Modifier.height(Spacing.md))
            DeltaRow(selected = selected, summary = summary)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.SummaryCell(
    value: String,
    label: String,
    accent: Boolean = false,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (accent) colors.accentDeep else colors.textPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun SummaryDivider() {
    Box(modifier = Modifier.size(width = 1.dp, height = 30.dp).background(BookechiTheme.colors.divider))
}

@Composable
private fun DeltaRow(selected: Int, summary: PeriodSummary) {
    val colors = BookechiTheme.colors
    if (selected == 2) {
        // Всё время: «N книг · с {месяц год}».
        Text(
            text = pluralStringResource(R.plurals.lib_books_count, summary.booksCount, summary.booksCount) +
                if (summary.sinceLabel.isNotEmpty()) " · " + stringResource(R.string.log_alltime_since, summary.sinceLabel) else "",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        return
    }
    val delta = summary.deltaPercent ?: 0
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "▲ +$delta %",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.accentDeep,
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.accentSoft)
                .padding(horizontal = 9.dp, vertical = 2.dp),
        )
        Spacer(Modifier.size(Spacing.sm))
        Text(
            text = stringResource(
                if (selected == 1) R.string.log_delta_month else R.string.log_delta_week
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

/* ----- Hint + toast ----- */

@Composable
private fun HintCard(onClose: () -> Unit) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardTint)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = colors.accentDeep, modifier = Modifier.size(18.dp))
        Text(
            text = stringResource(R.string.log_hint),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.Filled.Close,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(18.dp).clip(CircleShape).clickable(onClick = onClose),
        )
    }
}

@Composable
private fun LogToast(visible: Boolean, modifier: Modifier = Modifier) {
    if (!visible) return
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.textPrimary)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = colors.canvas, modifier = Modifier.size(18.dp))
        Text(
            text = stringResource(R.string.log_hint_toast),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.canvas,
        )
    }
}

/* ----- Day groups + rows ----- */

@Composable
private fun DayGroupHeader(bucket: Int, items: List<SessionLogItem>) {
    val colors = BookechiTheme.colors
    val pages = items.sumOf { it.pagesRead }
    val mins = items.sumOf { it.minutes }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.xl, bottom = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(bucketLabelRes(bucket)),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
        Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.divider))
        Text(
            text = "$pages ${stringResource(R.string.log_pages_short)} · ${timeText(mins)}",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun SessionRow(
    item: SessionLogItem,
    showCover: Boolean,
    onTap: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.stroke, RoundedCornerShape(18.dp))
            .clickable(enabled = item.isCurrent, onClick = onTap)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (showCover) {
            BookCover(
                coverPath = item.coverPath,
                title = item.title,
                author = item.author,
                width = 44.dp,
                shape = RoundedCornerShape(8.dp),
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    text = if (showCover) item.title else formatDateShort(item.date),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (item.isCurrent) CurrentTag()
            }
            if (showCover) {
                Text(
                    text = item.author.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                MetaChip(icon = Icons.Filled.MenuBook, text = "${item.fromPage} – ${item.toPage}")
                MetaChip(icon = Icons.Outlined.Schedule, text = stringResource(R.string.log_time_minutes, item.minutes))
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            if (showCover) {
                Text(
                    text = formatDateShort(item.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.height(2.dp))
            }
            Text(
                text = "+${item.pagesRead}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.accentDeep,
            )
            Text(
                text = stringResource(R.string.log_pages_short),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun CurrentTag() {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.accentSoft)
            .padding(start = 6.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = colors.accentDeep, modifier = Modifier.size(11.dp))
        Text(
            text = stringResource(R.string.log_current_tag).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            fontWeight = FontWeight.Bold,
            color = colors.accentDeep,
        )
    }
}

@Composable
private fun MetaChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.chipBg)
            .padding(horizontal = 9.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(icon, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(13.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = colors.textPrimary)
    }
}

@Composable
private fun EmptyLog(isBookScope: Boolean) {
    val colors = BookechiTheme.colors
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(if (isBookScope) R.string.log_empty_book else R.string.log_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
    }
}

/* ----- helpers ----- */

private fun groupByBucket(items: List<SessionLogItem>): List<Pair<Int, List<SessionLogItem>>> {
    if (items.isEmpty()) return emptyList()
    val byBucket = items.groupBy { bucketIndex(it.dayOffset) }
    return listOf(0, 1, 2, 3).mapNotNull { b -> byBucket[b]?.let { b to it } }
}

private fun bucketIndex(dayOffset: Int): Int = when {
    dayOffset <= 0 -> 0
    dayOffset == 1 -> 1
    dayOffset in 2..6 -> 2
    else -> 3
}

private fun bucketLabelRes(bucket: Int): Int = when (bucket) {
    0 -> R.string.log_group_today
    1 -> R.string.log_group_yesterday
    2 -> R.string.log_group_week
    else -> R.string.log_group_earlier
}

private fun formatDateShort(date: java.time.LocalDate): String =
    "%02d.%02d".format(date.dayOfMonth, date.monthValue)

private fun formatThousands(value: Int): String {
    val digits = kotlin.math.abs(value).toString()
    val chunked = digits.reversed().chunked(3).joinToString(" ").reversed()
    return (if (value < 0) "-" else "") + chunked
}

@Composable
private fun timeText(mins: Int): String = when {
    mins < 60 -> stringResource(R.string.log_time_minutes, mins)
    mins % 60 == 0 -> stringResource(R.string.log_time_hours, mins / 60)
    else -> stringResource(R.string.log_time_hours_minutes, mins / 60, mins % 60)
}

/* ----- Previews ----- */

private fun previewItems(): List<SessionLogItem> {
    val today = java.time.LocalDate.now()
    fun item(
        id: Long, title: String, author: String,
        from: Int, to: Int, mins: Int, offset: Int, current: Boolean,
    ) = SessionLogItem(
        sessionId = id, bookId = id, title = title, author = author, coverPath = null,
        fromPage = from, toPage = to, totalPages = 320, minutes = mins,
        date = today.minusDays(offset.toLong()), dayOffset = offset, isCurrent = current,
    )
    return listOf(
        item(1, "Норвежский лес", "Харуки Мураками", 135, 182, 74, 0, true),
        item(2, "Думай медленно… решай быстро", "Даниэль Канеман", 69, 120, 81, 0, true),
        item(3, "Над пропастью во ржи", "Дж. Д. Сэлинджер", 235, 272, 58, 0, false),
        item(4, "1984", "Джордж Оруэлл", 279, 328, 77, 0, false),
        item(5, "Норвежский лес", "Харуки Мураками", 99, 135, 57, 1, false),
        item(6, "1984", "Джордж Оруэлл", 8, 55, 74, 3, false),
    )
}

private val previewGlobalState = ReadingLogState(
    bookId = -1L,
    isBookScope = false,
    items = previewItems(),
    weekSummary = PeriodSummary(sessions = 13, pages = 498, minutes = 786, deltaPercent = 18),
    monthSummary = PeriodSummary(sessions = 41, pages = 1980, minutes = 2640, deltaPercent = -6),
    allTimeSummary = PeriodSummary(sessions = 120, pages = 24500, minutes = 31200, booksCount = 13, sinceLabel = "март 2024"),
    isLoading = false,
)

private val previewBookState = ReadingLogState(
    bookId = 1L,
    isBookScope = true,
    bookTitle = "Норвежский лес",
    items = previewItems().filter { it.bookId == 1L },
    isLoading = false,
)

@Preview(name = "ReadingLog Full Light", showBackground = true, backgroundColor = 0xFFF4ECE1, heightDp = 900)
@Composable
private fun ReadingLogFullPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ReadingLogContent(state = previewGlobalState, onAction = {})
        }
    }
}

@Preview(name = "ReadingLog Full Dark", showBackground = true, backgroundColor = 0xFF1C1611, heightDp = 900)
@Composable
private fun ReadingLogFullPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            ReadingLogContent(state = previewGlobalState, onAction = {})
        }
    }
}

@Preview(name = "ReadingLog Book Light", showBackground = true, backgroundColor = 0xFFF4ECE1, heightDp = 760)
@Composable
private fun ReadingLogBookPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ReadingLogContent(state = previewBookState, onAction = {})
        }
    }
}
