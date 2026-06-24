package fuwafuwa.time.bookechi.ui.feature.update_result.ui

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.PrimaryButton
import fuwafuwa.time.bookechi.base.ui.ds.ProgressBar
import fuwafuwa.time.bookechi.base.ui.ds.RatingStars
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.base.ui.ds.WarmTextField
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultAction
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultState
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.roundToInt

@Serializable
data class UpdateResultScreen(
    val startPages: Int,
    val updatedPages: Int,
    val bookAllPages: Int,
    val bookId: Long = -1L,
    val streakExtended: Boolean = false,
    val readingTimeMinutes: Int = 0,
    val bookName: String = "",
    val bookAuthor: String = "",
    val coverPath: String = "",
) : Screen

@Composable
fun UpdateResultScreen(
    viewModel: UpdateResultViewModel
) {
    val state by viewModel.model.state.collectAsState()

    UpdateResultScreenContent(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun UpdateResultScreenContent(
    state: UpdateResultState,
    onAction: (UpdateResultAction) -> Unit
) {
    // Цепочка для finished: стрик (оверлей) → результаты сессии → «Книга прочитана».
    var showFinished by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ReadingResultContent(
            state = state,
            // Анимацию запускаем после того, как уехала перебивка серии.
            play = !state.showStreakIntro && !showFinished,
            finished = state.isFinished,
            onPrimary = {
                if (state.isFinished) showFinished = true else onAction(UpdateResultAction.Done)
            },
        )

        if (state.isFinished && showFinished) {
            BookFinishedContent(state = state, onAction = onAction)
        }

        if (state.showStreakIntro && state.newStreakCount > 0) {
            StreakRenewedScreen(
                streak = state.newStreakCount,
                prevStreak = (state.newStreakCount - 1).coerceAtLeast(0),
                days = state.weekDays,
                onContinue = { onAction(UpdateResultAction.DismissStreakIntro) },
            )
        }
    }
}

@Composable
private fun FinishedContent(
    state: UpdateResultState,
    onAction: (UpdateResultAction) -> Unit,
) {
    val colors = BookechiTheme.colors

    FireTile()

    Spacer(Modifier.height(Spacing.xl))

    Text(
        text = stringResource(R.string.update_book_finished_title),
        style = MaterialTheme.typography.headlineMedium,
        color = colors.textPrimary,
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(Spacing.sm))

    Text(
        text = stringResource(R.string.update_book_finished_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textSecondary,
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(Spacing.xxl))

    // Карточка прогресса до 100% + строка «Сегодня прочитано».
    ProgressCard(state = state, finished = true)

    Spacer(Modifier.height(Spacing.md))

    TodayReadLine(pagesDelta = state.pagesDelta)

    Spacer(Modifier.height(Spacing.xxl))

    // Блок оценки.
    SectionLabel(text = stringResource(R.string.update_rating_label))

    Spacer(Modifier.height(Spacing.md))

    // TODO: persist rating & note (needs schema) — rating живёт только в State фичи.
    RatingStars(
        rating = state.rating,
        onRate = { onAction(UpdateResultAction.SetRating(it)) },
    )

    Spacer(Modifier.height(Spacing.lg))

    // TODO: persist rating & note (needs schema) — note живёт только в State фичи.
    WarmTextField(
        value = state.note,
        onValueChange = { onAction(UpdateResultAction.SetNote(it)) },
        label = stringResource(R.string.update_note_label),
        placeholder = stringResource(R.string.update_note_placeholder),
    )
}

@Composable
private fun FireTile() {
    val colors = BookechiTheme.colors
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(colors.accentSoft, DsShapes.tile),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
private fun TodayReadLine(pagesDelta: Int) {
    val colors = BookechiTheme.colors
    val absPages = abs(pagesDelta)
    val prefix = stringResource(R.string.update_today_read_prefix)
    val pagesPhrase = pluralStringResource(R.plurals.update_pages_count, absPages, absPages)
    Text(
        text = buildAnnotatedString {
            append(prefix)
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                append(pagesPhrase)
            }
        },
        style = MaterialTheme.typography.bodyLarge,
        color = colors.textSecondary,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ProgressCard(
    state: UpdateResultState,
    finished: Boolean = false,
) {
    val colors = BookechiTheme.colors

    val total = state.allBookPages.coerceAtLeast(1)
    val startPercent = (100f * state.startPages / total).roundToInt().coerceIn(0, 100)
    val newPercent = if (finished) 100 else (100f * state.updatedPages / total).roundToInt().coerceIn(0, 100)
    val newProgress = if (finished) 1f else (1f * state.updatedPages / total)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated, DsShapes.card)
            .border(1.dp, colors.stroke, DsShapes.card)
            .padding(Spacing.xl),
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = colors.textSecondary)) {
                    append("$startPercent%")
                }
                append("  →  ")
                withStyle(SpanStyle(color = colors.accentDeep, fontWeight = FontWeight.Bold)) {
                    append("$newPercent%")
                }
            },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(Spacing.md))

        ProgressBar(progress = newProgress, height = 10.dp)
    }
}

/* ============================================================================
 * Экран результатов сессии: акцент на приросте прогресса. Единый таймлайн (мс),
 * элементы появляются цепочкой. См. ReadingResultVisual.
 * ============================================================================ */

private const val RESULT_TOTAL_MS = 3900f

private val GainTop = Color(0xFFD98E63)
private val GainBottom = Color(0xFF9E4A2C)
private val OnAccentText = Color(0xFFFFF6EE)

/** clamp((t-start)/(end-start)) — доля фазы во времени. */
private fun rp(t: Float, start: Float, end: Float): Float =
    ((t - start) / (end - start)).coerceIn(0f, 1f)

/* ============================================================================
 * Экран 3 «Книга прочитана» (finished): полноцветная обложка + сэш «ПРОЧИТАНО»,
 * конфетти-бурст, оценка звёздами, плашка «N-я книга в этом году».
 * Тап по экрану / «Заново» — реплей анимаций.
 * ============================================================================ */

@Composable
private fun BookFinishedContent(
    state: UpdateResultState,
    onAction: (UpdateResultAction) -> Unit,
) {
    val colors = BookechiTheme.colors
    val context = LocalContext.current
    val reduceMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    var replay by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(colors.surface, colors.canvas),
                        center = Offset(size.width / 2f, size.height * 0.30f),
                        radius = size.maxDimension * 0.85f,
                    ),
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { replay++ },
    ) {
        if (!reduceMotion) {
            ConfettiBurst(animKey = replay, modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(56.dp))
            FinishedCover(state = state, animKey = replay, reduceMotion = reduceMotion)
            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = stringResource(R.string.result_finished_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.accentDeep,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(R.string.result_finished_books_behind, state.bookName, state.allBookPages),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.xl))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                (1..5).forEach { n ->
                    FinishStar(
                        index = n,
                        filled = n <= state.rating,
                        animKey = replay,
                        reduceMotion = reduceMotion,
                        onTap = { onAction(UpdateResultAction.SetRating(if (state.rating == n) 0 else n)) },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = if (state.rating > 0) {
                    stringResource(R.string.result_rating_value, state.rating)
                } else {
                    stringResource(R.string.result_rate_prompt)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.lg))
            WarmTextField(
                value = state.note,
                onValueChange = { onAction(UpdateResultAction.SetNote(it)) },
                placeholder = stringResource(R.string.result_note_placeholder),
                minLines = 2,
            )
            Spacer(Modifier.height(Spacing.lg))
            if (state.booksThisYear > 0) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.accentSoft)
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Icon(Icons.Filled.LocalFireDepartment, null, tint = colors.accent, modifier = Modifier.size(18.dp))
                    Text(
                        text = stringResource(R.string.result_nth_book_year, state.booksThisYear),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentDeep,
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xxl))
            PrimaryButton(
                text = stringResource(R.string.result_next),
                onClick = { onAction(UpdateResultAction.FinishContinue) },
            )
            Spacer(Modifier.height(Spacing.sm))
            // Менее акцентно — в стилистике кнопки «удалить сессию»: обводка, без заливки.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, colors.stroke, RoundedCornerShape(16.dp))
                    .clickable { onAction(UpdateResultAction.FinishToShelf) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.result_to_shelf),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                )
            }
            Spacer(Modifier.height(Spacing.xxl))
        }

        // «Заново» — реплей анимаций.
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(Spacing.md)
                .clip(CircleShape)
                .background(colors.surface)
                .clickable { replay++ }
                .padding(horizontal = Spacing.md, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(Icons.Filled.Refresh, null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
            Text(stringResource(R.string.result_replay), style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
        }
    }
}

@Composable
private fun FinishedCover(state: UpdateResultState, animKey: Int, reduceMotion: Boolean) {
    val pop = remember { Animatable(if (reduceMotion) 1f else 0.8f) }
    LaunchedEffect(animKey) {
        if (reduceMotion) {
            pop.snapTo(1f)
        } else {
            pop.snapTo(0.8f)
            pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow))
        }
    }
    Box(
        modifier = Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value },
        contentAlignment = Alignment.TopCenter,
    ) {
        BookCover(
            coverPath = state.coverPath,
            title = state.bookName,
            author = state.bookAuthor,
            width = 150.dp,
            shape = DsShapes.cover,
            modifier = Modifier.shadow(10.dp, DsShapes.cover, clip = false).clip(DsShapes.cover),
        )
        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .graphicsLayer { rotationZ = -6f }
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(GainTop, GainBottom)))
                .padding(horizontal = Spacing.md, vertical = 5.dp),
        ) {
            Text(
                text = stringResource(R.string.result_read_badge),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = OnAccentText,
            )
        }
    }
}

@Composable
private fun FinishStar(
    index: Int,
    filled: Boolean,
    animKey: Int,
    reduceMotion: Boolean,
    onTap: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val scale = remember { Animatable(if (reduceMotion) 1f else 0f) }
    LaunchedEffect(animKey) {
        if (reduceMotion) {
            scale.snapTo(1f)
        } else {
            scale.snapTo(0f)
            delay(index * 90L)
            scale.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium))
        }
    }
    Icon(
        imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarBorder,
        contentDescription = null,
        tint = if (filled) colors.accent else colors.divider,
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(CircleShape)
            .clickable { onTap() }
            .padding(2.dp),
    )
}

@Composable
private fun ConfettiBurst(animKey: Int, modifier: Modifier = Modifier) {
    val palette = listOf(
        Color(0xFFBE5E3B),
        Color(0xFF7C8A6E),
        Color(0xFFE0CFA8),
        Color(0xFFDDE3D2),
        Color(0xFF9E4A2C),
    )
    val t = remember { Animatable(0f) }
    LaunchedEffect(animKey) {
        t.snapTo(0f)
        t.animateTo(1f, tween(1900, easing = LinearEasing))
    }
    Canvas(modifier = modifier) {
        val n = 26
        val w = size.width
        val h = size.height
        val stripW = 6.dp.toPx()
        val stripH = 14.dp.toPx()
        for (i in 0 until n) {
            val delay = (i % 8) * 0.04f
            val denom = (1f - delay).coerceAtLeast(0.0001f)
            val p = ((t.value - delay) / denom).coerceIn(0f, 1f)
            if (p <= 0f) continue
            val x = w * (((i * 0.61803f) + 0.03f) % 1f)
            val y = -stripH + (h + stripH * 2f) * p
            val rot = p * (160f + i * 27f)
            val fade = 1f - ((p - 0.78f) / 0.22f).coerceIn(0f, 1f)
            rotate(degrees = rot, pivot = Offset(x, y)) {
                drawRoundRect(
                    color = palette[i % palette.size].copy(alpha = fade),
                    topLeft = Offset(x - stripW / 2f, y - stripH / 2f),
                    size = Size(stripW, stripH),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun ReadingResultContent(
    state: UpdateResultState,
    play: Boolean,
    finished: Boolean,
    onPrimary: () -> Unit,
) {
    val context = LocalContext.current
    val reduceMotion = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }

    val t = remember { Animatable(0f) }
    LaunchedEffect(play) {
        if (!play) return@LaunchedEffect
        if (reduceMotion) {
            t.snapTo(RESULT_TOTAL_MS)
        } else {
            t.animateTo(RESULT_TOTAL_MS, animationSpec = tween(RESULT_TOTAL_MS.toInt(), easing = LinearEasing))
        }
    }

    ReadingResultVisual(
        progressMs = t.value,
        reduceMotion = reduceMotion,
        state = state,
        finished = finished,
        onPrimary = onPrimary,
    )
}

@Composable
private fun ReadingResultVisual(
    progressMs: Float,
    reduceMotion: Boolean,
    state: UpdateResultState,
    finished: Boolean = false,
    onPrimary: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val tv = progressMs

    val total = state.allBookPages.coerceAtLeast(1)
    val before = (100f * state.startPages / total).coerceIn(0f, 100f)
    val after = if (state.isFinished) 100f else (100f * state.updatedPages / total).coerceIn(0f, 100f)
    val beforePct = before.roundToInt()
    val afterPct = after.roundToInt()
    val deltaPct = (afterPct - beforePct).coerceAtLeast(0)
    val deltaPages = state.pagesDelta.coerceAtLeast(0)

    // Фазы (мс).
    val coverScale = EaseOutBack.transform(rp(tv, 0f, 550f))
    val fillP = EaseOutCubic.transform(rp(tv, 1000f, 1850f))
    val braceAlpha = rp(tv, 1000f, 1400f)
    val behindSpring = EaseOutBack.transform(rp(tv, 1100f, 2000f))
    val behindAlpha = rp(tv, 1100f, 1500f)
    val counterP = EaseOutCubic.transform(rp(tv, 1100f, 2000f))
    val plaqueSpring = EaseOutBack.transform(rp(tv, 2150f, 2800f))
    val plaqueAlpha = rp(tv, 2150f, 2500f)
    val sweepP = rp(tv, 2600f, 3120f)
    val summaryP = EaseOutCubic.transform(rp(tv, 3000f, 3500f))
    val summaryAlpha = rp(tv, 3000f, 3400f)
    val buttonP = EaseOutCubic.transform(rp(tv, 3400f, 3850f))
    val buttonAlpha = rp(tv, 3400f, 3800f)

    val shownPct = (beforePct + (afterPct - beforePct) * counterP).roundToInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
            .navigationBarsPadding()
            .padding(start = Spacing.xxl, end = Spacing.xxl, top = Spacing.xl, bottom = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeroCover(
                state = state,
                before = before,
                after = after,
                fillP = fillP,
                braceAlpha = braceAlpha,
                deltaPages = deltaPages,
                coverScale = coverScale,
            )

            Spacer(Modifier.height(28.dp))

            DeltaPlaque(deltaPct = deltaPct, spring = plaqueSpring, alpha = plaqueAlpha, sweepP = sweepP)

            Spacer(Modifier.height(20.dp))

            BehindBlock(percent = shownPct, spring = behindSpring, alpha = behindAlpha)

            Spacer(Modifier.height(28.dp))

            SummaryRow(
                minutes = state.readingTimeMinutes,
                page = state.updatedPages,
                total = state.allBookPages,
                appearP = summaryP,
                alpha = summaryAlpha,
                reduceMotion = reduceMotion,
            )
        }

        PrimaryButton(
            text = stringResource(if (finished) R.string.result_next else R.string.update_done),
            onClick = onPrimary,
            modifier = Modifier.graphicsLayer {
                this.alpha = buttonAlpha
                translationY = (1f - buttonP) * 12.dp.toPx()
            },
        )
    }
}

@Composable
private fun HeroCover(
    state: UpdateResultState,
    before: Float,
    after: Float,
    fillP: Float,
    braceAlpha: Float,
    deltaPages: Int,
    coverScale: Float,
) {
    val colors = BookechiTheme.colors
    val coverW = 132.dp
    val coverH = 191.dp
    val leftArea = 100.dp
    val shape = RoundedCornerShape(16.dp)

    // Ч/б + затемнение для непрочитанной части (как на экране обновления прогресса).
    val dimPaint = remember {
        Paint().apply {
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
            alpha = 0.45f
        }
    }

    val currentPct = before + (after - before) * fillP
    val midDp = coverH * (((1f - currentPct / 100f) + (1f - before / 100f)) / 2f)

    Box(
        modifier = Modifier
            .width(leftArea + coverW)
            .height(coverH)
            .graphicsLayer { scaleX = coverScale; scaleY = coverScale },
    ) {
        // Обложка с прогресс-визуализацией.
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(coverW)
                .height(coverH)
                .clip(shape),
        ) {
            // База: вся обложка обесцвечена и притушена (ч/б).
            BookCover(
                coverPath = state.coverPath,
                title = state.bookName.ifBlank { "—" },
                author = state.bookAuthor,
                width = null,
                shape = shape,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawIntoCanvas { canvas ->
                            canvas.saveLayer(Rect(Offset.Zero, size), dimPaint)
                            drawContent()
                            canvas.restore()
                        }
                    },
            )
            // Полноцветная прочитанная часть снизу до текущей ватерлинии (after).
            BookCover(
                coverPath = state.coverPath,
                title = state.bookName.ifBlank { "—" },
                author = state.bookAuthor,
                width = null,
                shape = shape,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        clipRect(top = size.height * (1f - currentPct / 100f)) {
                            this@drawWithContent.drawContent()
                        }
                    },
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val waterY = h * (1f - currentPct / 100f)
                val beforeY = h * (1f - before / 100f)
                // Полоса диффа за сессию (before→after): полупрозрачная тёплая
                // подсветка — обложка видна сквозь неё, не перекрывается.
                if (beforeY > waterY) {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(GainTop, GainBottom), startY = waterY, endY = beforeY),
                        topLeft = Offset(0f, waterY),
                        size = Size(w, beforeY - waterY),
                        alpha = braceAlpha * 0.45f,
                    )
                    // Нижняя граница диффа (before).
                    drawLine(
                        Color.White.copy(alpha = braceAlpha * 0.6f),
                        Offset(0f, beforeY),
                        Offset(w, beforeY),
                        strokeWidth = 1.5.dp.toPx(),
                    )
                }
                // Верхняя граница (after) — ватерлиния (акцент, без обводки).
                // У самого верха линия выпирала бы за скруглённый край обложки —
                // гасим её на последних ~6dp перед «потолком».
                val topFade = ((waterY - 1.5.dp.toPx()) / 6.dp.toPx()).coerceIn(0f, 1f)
                if (topFade > 0f) {
                    drawLine(
                        colors.accent.copy(alpha = braceAlpha * topFade),
                        Offset(0f, waterY),
                        Offset(w, waterY),
                        strokeWidth = 3.dp.toPx(),
                    )
                }
            }
        }
        // Фигурная скобка, обнимающая полосу прироста.
        Canvas(modifier = Modifier.fillMaxSize()) {
            val coverLeft = leftArea.toPx()
            val xr = coverLeft - 6.dp.toPx()
            val xl = xr - 12.dp.toPx()
            val y0 = size.height * (1f - currentPct / 100f)
            val y1 = size.height * (1f - before / 100f)
            if (y1 - y0 >= 4.dp.toPx()) {
                // Высоты хватает — фигурная скобка вдоль полосы диффа.
                drawPath(
                    path = braceLeftPath(xl, xr, y0, y1),
                    color = colors.accent.copy(alpha = braceAlpha),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                )
            } else {
                // Дифф маленький — скобка не влезает: точка-маркер на ватерлинии
                // у левого края обложки.
                drawCircle(
                    color = colors.accent.copy(alpha = braceAlpha),
                    radius = 4.dp.toPx(),
                    center = Offset(coverLeft - 3.dp.toPx(), y0),
                )
            }
        }
        // Чип «+N стр» по центру полосы прироста — в левой дорожке, прижат к её
        // правому краю с зазором до скобки (leftArea − ~24dp), чтобы не залезать на неё.
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(leftArea - 24.dp)
                .offset(y = midDp - 13.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier = Modifier
                    // Не сжимаемся под ширину дорожки — растём по контенту и
                    // вылезаем влево, чтобы текст оставался в одну строку.
                    .wrapContentWidth(align = Alignment.End, unbounded = true)
                    .graphicsLayer { this.alpha = braceAlpha }
                    .clip(CircleShape)
                    .background(colors.accent)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(R.string.result_pages_chip, deltaPages),
                    style = MaterialTheme.typography.labelMedium,
                    color = OnAccentText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

private fun braceLeftPath(xl: Float, xr: Float, y0: Float, y1: Float): Path {
    val midY = (y0 + y1) / 2f
    return Path().apply {
        moveTo(xr, y0)
        cubicTo(xl, y0, xr, midY, xl, midY)
        cubicTo(xr, midY, xl, y1, xr, y1)
    }
}

@Composable
private fun DeltaPlaque(deltaPct: Int, spring: Float, alpha: Float, sweepP: Float) {
    val colors = BookechiTheme.colors
    val shape = RoundedCornerShape(38.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    this.alpha = alpha
                    val s = 0.6f + 0.4f * spring
                    scaleX = s
                    scaleY = s
                    translationY = (1f - spring) * 40.dp.toPx()
                    // Без offscreen-буфера — иначе тень бейджа обрезается во время fade.
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                }
                .shadow(
                    elevation = 12.dp,
                    shape = shape,
                    ambientColor = GainBottom,
                    spotColor = GainBottom,
                )
                .clip(shape)
                .background(Brush.horizontalGradient(listOf(GainTop, GainBottom))),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "+$deltaPct%",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = OnAccentText,
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = OnAccentText,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            // Блик-шиммер: матовая полоса, наклонённая «/», едущая строго по
            // горизонтали слева направо. Полосу рисуем высокой и узкой, поворачиваем
            // вокруг её центра, а центр двигаем только по X → линия диагональ,
            // движение ровное.
            if (sweepP > 0f && sweepP < 1f) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val bandW = size.width * 0.30f
                    val cx = -bandW + (size.width + 2f * bandW) * sweepP
                    val cy = size.height / 2f
                    val bigH = size.height * 2.4f
                    rotate(degrees = 22f, pivot = Offset(cx, cy)) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                0.0f to Color.Transparent,
                                0.5f to Color.White.copy(alpha = 0.26f),
                                1.0f to Color.Transparent,
                                startX = cx - bandW / 2f,
                                endX = cx + bandW / 2f,
                            ),
                            topLeft = Offset(cx - bandW / 2f, cy - bigH / 2f),
                            size = Size(bandW, bigH),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.result_session_progress),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.graphicsLayer { this.alpha = alpha },
        )
    }
}

@Composable
private fun BehindBlock(percent: Int, spring: Float, alpha: Float) {
    val colors = BookechiTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            val s = 0.85f + 0.15f * spring
            scaleX = s
            scaleY = s
            translationY = (1f - spring) * 10.dp.toPx()
        },
    ) {
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = colors.accentDeep,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.result_book_behind),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun SummaryRow(
    minutes: Int,
    page: Int,
    total: Int,
    appearP: Float,
    alpha: Float,
    reduceMotion: Boolean,
) {
    val colors = BookechiTheme.colors
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = (1f - appearP) * 12.dp.toPx()
            }
            .clip(shape)
            .background(colors.cardTint)
            .border(1.dp, colors.stroke, shape)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SummaryCell(
            modifier = Modifier.weight(1f),
            icon = { TickingClock(modifier = Modifier.size(22.dp), tint = colors.accent, reduceMotion = reduceMotion) },
            value = stringResource(R.string.result_minutes_value, minutes),
            caption = stringResource(R.string.result_session_caption),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(colors.divider),
        )
        SummaryCell(
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(22.dp),
                )
            },
            value = "$page / $total",
            caption = stringResource(R.string.result_page_caption),
        )
    }
}

@Composable
private fun SummaryCell(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    value: String,
    caption: String,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        icon()
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun TickingClock(modifier: Modifier, tint: Color, reduceMotion: Boolean) {
    val inf = rememberInfiniteTransition(label = "clock")
    val minute by inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "minute",
    )
    val hour by inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(72000, easing = LinearEasing), RepeatMode.Restart),
        label = "hour",
    )
    val minA = if (reduceMotion) 0f else minute
    val hrA = if (reduceMotion) 30f else hour
    Canvas(modifier = modifier) {
        val r = size.minDimension / 2f
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(color = tint, radius = r - 1.dp.toPx(), style = Stroke(width = 1.5.dp.toPx()))
        rotate(degrees = hrA, pivot = c) {
            drawLine(tint, c, Offset(c.x, c.y - r * 0.45f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        }
        rotate(degrees = minA, pivot = c) {
            drawLine(tint, c, Offset(c.x, c.y - r * 0.72f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

private val previewSessionState = UpdateResultState(
    pagesDelta = 24,
    startPages = 182,
    updatedPages = 206,
    allBookPages = 320,
    newStreakCount = 5,
    readingTimeMinutes = 47,
    bookName = "Норвежский лес",
    bookAuthor = "Харуки Мураками",
)

@Preview(name = "UpdateResult Session Light", showBackground = true, backgroundColor = 0xFFF4ECE1, heightDp = 780)
@Composable
private fun UpdateResultSessionPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ReadingResultVisual(
                progressMs = RESULT_TOTAL_MS,
                reduceMotion = true,
                state = previewSessionState,
                onPrimary = {},
            )
        }
    }
}

@Preview(name = "UpdateResult Session Dark", showBackground = true, backgroundColor = 0xFF1C1611, heightDp = 780)
@Composable
private fun UpdateResultSessionPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            ReadingResultVisual(
                progressMs = RESULT_TOTAL_MS,
                reduceMotion = true,
                state = previewSessionState,
                onPrimary = {},
            )
        }
    }
}

@Preview(name = "UpdateResult Finished Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun UpdateResultFinishedPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateResultScreenContent(
                state = UpdateResultState(
                    pagesDelta = 32,
                    startPages = 224,
                    updatedPages = 256,
                    allBookPages = 256,
                    newStreakCount = 5,
                    rating = 4,
                    note = "",
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "UpdateResult Finished Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun UpdateResultFinishedPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateResultScreenContent(
                state = UpdateResultState(
                    pagesDelta = 32,
                    startPages = 224,
                    updatedPages = 256,
                    allBookPages = 256,
                    newStreakCount = 5,
                    rating = 4,
                    note = "Любимая цитата",
                ),
                onAction = {},
            )
        }
    }
}
