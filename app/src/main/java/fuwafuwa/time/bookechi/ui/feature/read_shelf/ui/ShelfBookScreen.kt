package fuwafuwa.time.bookechi.ui.feature.read_shelf.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.CornerBadgeSpec
import fuwafuwa.time.bookechi.base.ui.ds.CoverCornerCutoutShape
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.RatingSheet
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.badgeShape
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfBookAction
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfBookState
import fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi.ShelfBookViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.serialization.Serializable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Serializable
data class ShelfBookScreen(val bookId: Long) : Screen

@Composable
fun ShelfBookScreen(viewModel: ShelfBookViewModel) {
    val state by viewModel.model.state.collectAsState()
    ShelfBookContent(state = state, onAction = viewModel::sendAction)
}

@Composable
private fun ShelfBookContent(
    state: ShelfBookState,
    onAction: (ShelfBookAction) -> Unit,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.surface)
                    .clickable { onAction(ShelfBookAction.NavigateBack) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = colors.textPrimary, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.shelf_detail_crumb, state.finishedAt?.let { dateFull(it) } ?: "—"),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // Cover on mini-shelf + right column
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CoverOnShelf(
                    coverPath = state.coverPath,
                    title = state.title,
                    author = state.author,
                    favorite = state.favorite,
                    onToggleFavorite = { onAction(ShelfBookAction.ToggleFavorite) },
                )
                Spacer(Modifier.height(Spacing.md))
                Row(
                    modifier = Modifier
                        .width(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.accentSoft)
                        .clickable { onAction(ShelfBookAction.OpenBookPage) }
                        .padding(vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Filled.MenuBook, null, tint = colors.accentDeep, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(Spacing.sm))
                    Text(stringResource(R.string.shelf_book_page), style = MaterialTheme.typography.labelMedium, color = colors.accentDeep, textAlign = TextAlign.Center)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    text = state.author.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                // rating (тап → шторка оценки)
                InfoCard(
                    label = stringResource(R.string.shelf_my_rating),
                    onClick = { onAction(ShelfBookAction.OpenRatingSheet) },
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        repeat(5) { i ->
                            Icon(
                                Icons.Rounded.Star,
                                null,
                                tint = if (i < state.rating) colors.accent else colors.textSecondary.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (state.rating > 0) "${state.rating},0" else "—",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state.rating > 0) colors.accentDeep else colors.textSecondary,
                        )
                    }
                }
                // read dates
                InfoCard(label = stringResource(R.string.shelf_read_label)) {
                    Text(
                        text = when {
                            state.startedAt == null || state.finishedAt == null -> "—"
                            state.startedAt == state.finishedAt -> dateShort(state.startedAt)
                            else -> "${dateShort(state.startedAt)} —    ${dateShort(state.finishedAt)}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = pluralStringResource(R.plurals.shelf_days, state.daysCount, state.daysCount) +
                            " · " + pluralStringResource(R.plurals.shelf_sessions, state.sessionsCount, state.sessionsCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // 3 tiles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceElevated)
                .padding(vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Tile(value = state.pages.toString(), label = stringResource(R.string.shelf_tile_pages))
            TileDivider()
            Tile(value = state.sessionsCount.toString(), label = stringResource(R.string.shelf_tile_sessions))
            TileDivider()
            Tile(value = timeText(state.totalMinutes), label = stringResource(R.string.shelf_tile_time))
        }

        Spacer(Modifier.height(Spacing.lg))

        Text(
            text = stringResource(R.string.shelf_progress_title).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(Spacing.sm))
        ProgressChartCard(state = state)

        Spacer(Modifier.height(Spacing.xxl))
    }

    if (state.isRatingSheetOpen) {
        RatingSheet(
            current = state.rating,
            onDismiss = { onAction(ShelfBookAction.CloseRatingSheet) },
            onSave = { onAction(ShelfBookAction.SetRating(it)) },
            onClear = { onAction(ShelfBookAction.SetRating(0)) },
        )
    }
}


@Composable
private fun CoverOnShelf(
    coverPath: String?,
    title: String,
    author: String,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val dark = colors.isDark
    val badge = remember {
        CornerBadgeSpec(size = 30.dp, gap = 4.dp, cornerOuter = 8.dp, cornerSmall = 6.dp, cornerInner = 16.dp, edgeRound = 6.dp)
    }
    val coverShape = CoverCornerCutoutShape(8.dp, badge, bottomRadius = 0.dp)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            BookCover(
                coverPath = coverPath,
                title = title,
                author = author,
                width = 132.dp,
                shape = coverShape,
                titleEndInset = badge.size,
                modifier = Modifier
                    .shadow(6.dp, coverShape, clip = false)
                    .clip(coverShape),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(badge.size)
                    .clip(badge.badgeShape())
                    .background(if (favorite) colors.accent else colors.accentSoft)
                    .clickable(onClick = onToggleFavorite),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = if (favorite) Color.White else colors.accentDeep,
                    modifier = Modifier.size(15.dp),
                )
            }
        }
        Column(
            modifier = Modifier
                .width(150.dp)
                .shadow(6.dp, RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)),
        ) {
            Box(Modifier.fillMaxWidth().height(8.dp).background(Brush.verticalGradient(
                if (dark) listOf(Color(0xFF6E5638), Color(0xFF5C4630)) else listOf(Color(0xFFE6C79E), Color(0xFFD3A674))
            )))
            Box(Modifier.fillMaxWidth().height(10.dp).background(Brush.verticalGradient(
                if (dark) listOf(Color(0xFF4A3826), Color(0xFF3A2C1E)) else listOf(Color(0xFFB78C58), Color(0xFF9C744A))
            )))
        }
    }
}

@Composable
private fun InfoCard(label: String, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.cardTint)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
        content()
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.Tile(value: String, label: String) {
    val colors = BookechiTheme.colors
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
    }
}

@Composable
private fun TileDivider() {
    Box(Modifier.size(width = 1.dp, height = 32.dp).background(BookechiTheme.colors.divider))
}

@Composable
private fun ProgressChartCard(state: ShelfBookState) {
    val colors = BookechiTheme.colors
    val density = LocalDensity.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceElevated)
            .padding(Spacing.lg),
    ) {
        // Книга прочитана за одну сессию — графику нечего показывать.
        if (state.progress.size <= 1) {
            Box(
                modifier = Modifier.fillMaxWidth().height(96.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Icon(Icons.Filled.LocalFireDepartment, null, tint = colors.accent, modifier = Modifier.size(28.dp))
                    Text(
                        text = stringResource(R.string.shelf_one_sitting),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentDeep,
                    )
                }
            }
            return@Column
        }
        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val padL = with(density) { 6.dp.toPx() }
                val padR = with(density) { 6.dp.toPx() }
                val padT = with(density) { 26.dp.toPx() }
                val padB = with(density) { 22.dp.toPx() }
                val w = size.width
                val h = size.height
                drawLine(
                    color = colors.textSecondary.copy(alpha = 0.35f),
                    start = Offset(padL, padT),
                    end = Offset(w - padR, padT),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx())),
                )
                val baseY = h - padB
                drawLine(
                    color = colors.textSecondary.copy(alpha = 0.25f),
                    start = Offset(padL, baseY),
                    end = Offset(w - padR, baseY),
                    strokeWidth = 1.dp.toPx(),
                )
                val pts = state.progress
                if (pts.isNotEmpty()) {
                    val n = pts.size
                    fun px(i: Int) = if (n == 1) (padL + w - padR) / 2f else padL + (w - padL - padR) * i / (n - 1)
                    fun py(v: Float) = baseY - (baseY - padT) * v.coerceIn(0f, 1f)
                    if (n > 1) {
                        val fill = Path().apply {
                            moveTo(px(0), baseY)
                            for (i in 0 until n) lineTo(px(i), py(pts[i]))
                            lineTo(px(n - 1), baseY)
                            close()
                        }
                        drawPath(
                            path = fill,
                            brush = Brush.verticalGradient(
                                colors = listOf(colors.accent.copy(alpha = 0.28f), colors.accent.copy(alpha = 0f)),
                                startY = padT,
                                endY = baseY,
                            ),
                        )
                    }
                    for (i in 0 until n - 1) {
                        drawLine(
                            color = colors.accent,
                            start = Offset(px(i), py(pts[i])),
                            end = Offset(px(i + 1), py(pts[i + 1])),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    for (i in 1 until n) {
                        drawCircle(color = colors.surfaceElevated, radius = 3.5.dp.toPx(), center = Offset(px(i), py(pts[i])))
                        drawCircle(color = colors.accent, radius = 3.5.dp.toPx(), center = Offset(px(i), py(pts[i])), style = Stroke(width = 1.5.dp.toPx()))
                    }
                    drawCircle(color = colors.accentDeep, radius = 4.dp.toPx(), center = Offset(px(n - 1), py(pts[n - 1])))
                }
            }
            Text("100%", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, modifier = Modifier.align(Alignment.TopEnd))
            if (state.recordPages > 0) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(colors.accent)
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(Icons.Filled.Bolt, null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Text(stringResource(R.string.shelf_record, state.recordPages), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            state.startedAt?.let {
                Text(dateShort(it), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, modifier = Modifier.align(Alignment.BottomStart))
            }
            state.finishedAt?.let {
                Text(dateShort(it), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, modifier = Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}

@Composable
private fun timeText(mins: Int): String = when {
    mins < 60 -> stringResource(R.string.log_time_minutes, mins)
    mins % 60 == 0 -> stringResource(R.string.log_time_hours, mins / 60)
    else -> stringResource(R.string.log_time_hours_minutes, mins / 60, mins % 60)
}

private fun dateShort(d: LocalDate): String {
    val m = Month.of(d.monthValue).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()).trimEnd('.')
    return "${d.dayOfMonth} $m"
}

private fun dateFull(d: LocalDate): String = "${dateShort(d)} ${d.year}"

/* ----- Previews ----- */

private fun previewBookState(): ShelfBookState = ShelfBookState(
    bookId = 1L,
    title = "Над пропастью во ржи",
    author = "Дж. Д. Сэлинджер",
    coverPath = null,
    favorite = true,
    pages = 272,
    sessionsCount = 14,
    totalMinutes = 700,
    startedAt = LocalDate.of(2026, 5, 12),
    finishedAt = LocalDate.of(2026, 6, 3),
    daysCount = 22,
    progress = listOf(0.04f, 0.09f, 0.15f, 0.22f, 0.3f, 0.38f, 0.45f, 0.53f, 0.62f, 0.7f, 0.79f, 0.88f, 0.95f, 1f),
    recordPages = 36,
    rating = 5,
    book = null,
    isLoading = false,
)

@Preview(name = "ShelfBook Light", showBackground = true, backgroundColor = 0xFFF4ECE1, heightDp = 820)
@Composable
private fun ShelfBookPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ShelfBookContent(state = previewBookState(), onAction = {})
        }
    }
}

@Preview(name = "ShelfBook Dark", showBackground = true, backgroundColor = 0xFF1C1611, heightDp = 820)
@Composable
private fun ShelfBookPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            ShelfBookContent(state = previewBookState(), onAction = {})
        }
    }
}
