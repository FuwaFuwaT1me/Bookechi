package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.SectionLabel
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ActivityChartTab
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.russianMonthYear
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

@Composable
fun ActivityPanel(
    state: ProductivityState,
    onToggleActivityChartSwitch: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        PeriodSwitcher(state, onToggleActivityChartSwitch)
        HeatmapCard(state)
        AnimatedVisibility(
            visible = state.activityChartTab == ActivityChartTab.YEAR,
            enter = fadeIn(tween(220)) + expandVertically(tween(280)),
            exit = fadeOut(tween(160)) + shrinkVertically(tween(220)),
        ) {
            YearSummaryCard(state)
        }
    }
}

/**
 * Сегментированный pill «Месяц / Год».
 * Контейнер — chipBg (pill); активный сегмент — surfaceElevated с stroke и accentDeep-текстом.
 */
@Composable
private fun PeriodSwitcher(
    state: ProductivityState,
    onSwitch: (Int) -> Unit
) {
    val colors = BookechiTheme.colors
    val tabs = listOf(
        stringResource(R.string.prod_tab_month),
        stringResource(R.string.prod_tab_year),
    )
    val selectedIndex = state.activityChartTab.ordinal

    // Доля смещения бегунка: 0 — левый сегмент, 1 — правый. Анимируется.
    val thumbFraction by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = 260),
        label = "thumbFraction",
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colors.chipBg, DsShapes.pill)
            .padding(Spacing.xs),
    ) {
        val thumbWidth = maxWidth / tabs.size

        // Скользящий бегунок под активным сегментом.
        Box(
            modifier = Modifier
                .width(thumbWidth)
                .fillMaxHeight()
                .offset(x = thumbWidth * thumbFraction)
                .clip(DsShapes.pill)
                .background(colors.surfaceElevated)
                .border(BorderStroke(1.dp, colors.stroke), DsShapes.pill),
        )

        // Подписи поверх бегунка.
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, title ->
                val textColor by animateColorAsState(
                    targetValue = if (selectedIndex == index) colors.accentDeep else colors.textSecondary,
                    label = "segmentText",
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSwitch(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = textColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeatmapCard(
    state: ProductivityState
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated, DsShapes.card)
            .border(BorderStroke(1.dp, colors.stroke), DsShapes.card)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = periodTitle(state),
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
        )

        Text(
            text = chartSubtitle(state),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        AnimatedContent(
            targetState = state.activityChartTab,
            transitionSpec = {
                (fadeIn(tween(220)) togetherWith fadeOut(tween(180)))
                    .using(SizeTransform(clip = false) { _, _ -> tween(300) })
            },
            label = "activityChart",
        ) { tab ->
            when (tab) {
                ActivityChartTab.MONTH -> {
                    MonthActivityChart(
                        year = state.currentYear,
                        month = state.currentMonth,
                        sessions = state.sessions,
                        config = ActivityChartConfig(
                            showSpacing = true,
                            itemHorizontalSpacing = Spacing.xs,
                            itemVerticalSpacing = Spacing.xs,
                            cornerRadius = 6.dp,
                        ),
                    )
                }
                ActivityChartTab.YEAR -> {
                    YearActivityChart(
                        year = state.currentYear,
                        sessions = state.sessions,
                    )
                }
            }
        }
    }
}

@Composable
private fun periodTitle(state: ProductivityState): String {
    return when (state.activityChartTab) {
        ActivityChartTab.MONTH -> russianMonthYear(state.currentMonth, state.currentYear)
        ActivityChartTab.YEAR -> stringResource(R.string.prod_year_title, state.currentYear)
    }
}

@Composable
private fun chartSubtitle(state: ProductivityState): String = when (state.activityChartTab) {
    ActivityChartTab.MONTH -> stringResource(R.string.prod_subtitle_month)
    ActivityChartTab.YEAR -> stringResource(R.string.prod_subtitle_year)
}

/** Карточка «Итоги года» под годовым графиком (макет year chart). */
@Composable
private fun YearSummaryCard(state: ProductivityState) {
    val colors = BookechiTheme.colors
    val monthly = IntArray(12)
    state.sessions.forEach { session ->
        val date = session.localDate
        if (date.year == state.currentYear) {
            monthly[date.monthValue - 1] += session.totalPagesRead
        }
    }
    val totalPages = monthly.sum()
    val bestMonthIndex = monthly.indices.maxByOrNull { monthly[it] } ?: 0
    val hasData = totalPages > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated, DsShapes.card)
            .border(BorderStroke(1.dp, colors.stroke), DsShapes.card)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        SectionLabel(text = stringResource(R.string.prod_year_summary_title))
        Spacer(modifier = Modifier.height(Spacing.xs))
        YearSummaryRow(
            label = stringResource(R.string.prod_year_total_pages),
            value = formatThousands(totalPages),
        )
        YearSummaryRow(
            label = stringResource(R.string.prod_year_books_finished),
            value = state.booksRead.toString(),
        )
        YearSummaryRow(
            label = stringResource(R.string.prod_year_best_month),
            value = if (hasData) fullMonthName(bestMonthIndex) else "—",
            emphasized = true,
        )
    }
}

@Composable
private fun YearSummaryRow(label: String, value: String, emphasized: Boolean = false) {
    val colors = BookechiTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (emphasized) FontWeight.Bold else FontWeight.SemiBold,
            ),
            color = colors.textPrimary,
        )
    }
}

/** Полное имя месяца по индексу 0..11. */
@Composable
private fun fullMonthName(index: Int): String = when (index) {
    0 -> stringResource(R.string.prod_month_january)
    1 -> stringResource(R.string.prod_month_february)
    2 -> stringResource(R.string.prod_month_march)
    3 -> stringResource(R.string.prod_month_april)
    4 -> stringResource(R.string.prod_month_may)
    5 -> stringResource(R.string.prod_month_june)
    6 -> stringResource(R.string.prod_month_july)
    7 -> stringResource(R.string.prod_month_august)
    8 -> stringResource(R.string.prod_month_september)
    9 -> stringResource(R.string.prod_month_october)
    10 -> stringResource(R.string.prod_month_november)
    11 -> stringResource(R.string.prod_month_december)
    else -> ""
}

private fun formatThousands(n: Int): String =
    n.toString().reversed().chunked(3).joinToString(" ").reversed()

@Preview(name = "ActivityPanel Month Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun PreviewActivityPanelMonthLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            ActivityPanel(
                state = ProductivityState(
                    sessions = ProductivityPreviewData.generateMonthData(year = 2026, month = 6),
                    currentYear = 2026,
                    currentMonth = 6,
                ),
                onToggleActivityChartSwitch = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@Preview(name = "ActivityPanel Year Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun PreviewActivityPanelYearDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            ActivityPanel(
                state = ProductivityState(
                    sessions = ProductivityPreviewData.generateYearData(),
                    booksRead = 12,
                    activityChartTab = ActivityChartTab.YEAR,
                    currentYear = 2026,
                    currentMonth = 6,
                ),
                onToggleActivityChartSwitch = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}
