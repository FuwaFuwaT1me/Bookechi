package fuwafuwa.time.bookechi.ui.feature.update_result.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
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
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.roundToInt

@Serializable
data class UpdateResultScreen(
    val startPages: Int,
    val updatedPages: Int,
    val bookAllPages: Int,
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
    val colors = BookechiTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xxl, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Spacing.xxxl))

        if (state.isFinished) {
            FinishedContent(state = state, onAction = onAction)
        } else {
            StreakContent(state = state)
        }

        Spacer(Modifier.height(Spacing.xxxl))

        PrimaryButton(
            text = stringResource(R.string.update_done),
            onClick = { onAction(UpdateResultAction.Done) },
        )

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun StreakContent(state: UpdateResultState) {
    val colors = BookechiTheme.colors

    // 1) Тайл с огоньком.
    FireTile()

    Spacer(Modifier.height(Spacing.xl))

    // 2) «{newStreakCount} дней подряд».
    Text(
        text = pluralStringResource(
            R.plurals.update_streak_days_in_row,
            state.newStreakCount,
            state.newStreakCount,
        ),
        style = MaterialTheme.typography.headlineMedium,
        color = colors.textPrimary,
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(Spacing.md))

    // 3) «Сегодня прочитано: {pagesDelta} страниц».
    TodayReadLine(pagesDelta = state.pagesDelta)

    Spacer(Modifier.height(Spacing.xxl))

    // 4) Карточка прогресса.
    ProgressCard(state = state)

    Spacer(Modifier.height(Spacing.xxl))

    // 5) Тёплая фраза курсивом.
    Text(
        text = stringResource(R.string.update_cozy_phrase),
        style = MaterialTheme.typography.titleLarge.copy(fontStyle = FontStyle.Italic),
        color = colors.textSecondary,
        textAlign = TextAlign.Center,
    )
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

@Preview(name = "UpdateResult Streak Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun UpdateResultStreakPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateResultScreenContent(
                state = UpdateResultState(
                    pagesDelta = 15,
                    startPages = 54,
                    updatedPages = 69,
                    allBookPages = 256,
                    newStreakCount = 5,
                ),
                onAction = {},
            )
        }
    }
}

@Preview(name = "UpdateResult Streak Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun UpdateResultStreakPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            UpdateResultScreenContent(
                state = UpdateResultState(
                    pagesDelta = 15,
                    startPages = 54,
                    updatedPages = 69,
                    allBookPages = 256,
                    newStreakCount = 5,
                ),
                onAction = {},
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
