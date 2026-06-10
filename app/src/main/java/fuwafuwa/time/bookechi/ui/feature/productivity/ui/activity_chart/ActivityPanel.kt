package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
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
    val tabs = listOf("Месяц", "Год")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colors.chipBg, DsShapes.pill)
            .padding(Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = state.activityChartTab.ordinal == index
            val segmentBg by animateColorAsState(
                targetValue = if (selected) colors.surfaceElevated else colors.chipBg,
                label = "segmentBg"
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) colors.accentDeep else colors.textSecondary,
                label = "segmentText"
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(DsShapes.pill)
                    .background(segmentBg)
                    .then(
                        if (selected) {
                            Modifier.border(BorderStroke(1.dp, colors.stroke), DsShapes.pill)
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSwitch(index) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
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

        ActivityLegend()

        Spacer(modifier = Modifier.height(Spacing.sm))

        when (state.activityChartTab) {
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
                    config = ActivityChartConfig(cornerRadius = 10.dp),
                )
            }
        }
    }
}

private fun periodTitle(state: ProductivityState): String {
    return when (state.activityChartTab) {
        ActivityChartTab.MONTH -> russianMonthYear(state.currentMonth, state.currentYear)
        ActivityChartTab.YEAR -> "${state.currentYear} год"
    }
}

@Composable
private fun ActivityLegend() {
    val colors = BookechiTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Меньше",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        ActivityChartLegend(config = ActivityChartConfig(cornerRadius = 2.dp))

        Spacer(modifier = Modifier.width(Spacing.sm))

        Text(
            text = "Больше",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
    }
}

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
