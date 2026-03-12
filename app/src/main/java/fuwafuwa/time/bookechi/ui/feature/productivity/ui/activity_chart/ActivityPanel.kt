package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ActivityColorScheme
import fuwafuwa.time.bookechi.base.ui.util.AnimatedPeriodSwitcher
import fuwafuwa.time.bookechi.base.ui.util.AnimatedPeriodSwitcherConfig
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ActivityChartTab
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaPeriodSwitcherBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaRedTitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
fun ActivityPanel(
    state: ProductivityState,
    onToggleActivityChartSwitch: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = FigmaBackground,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 20.dp)
        ,
    ) {
        PeriodSwitcher(state, onToggleActivityChartSwitch)

        Spacer(modifier = Modifier.height(10.dp))

        ActivityPart(state)

        Spacer(modifier = Modifier.height(24.dp))

        ReadPagesOverview(state)
    }
}

@Composable
private fun PeriodSwitcher(
    state: ProductivityState,
    onSwitch: (Int) -> Unit
) {
    AnimatedPeriodSwitcher(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ,
        values = listOf("Месяц", "Год"),
        innerCornerRadius = 8.dp,
        outerCornerRadius = 12.dp,
        horizontalSpacing = 4.dp,
        verticalSpacing = 4.dp,
        itemSpacing = 8.dp,
        config = AnimatedPeriodSwitcherConfig(
            containerColor = FigmaPeriodSwitcherBackground,
            selectedColor = Color.White,
            activeTextColor = FigmaTitle,
            inactiveTextColor = FigmaTitle.copy(alpha = 0.4f)
        ),
        selectedIndex = state.activityChartTab.ordinal,
        onSwitch = { index ->
            onSwitch(index)
        }
    )
}

@Composable
private fun ActivityPart(
    state: ProductivityState
) {
    Column(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
        ,
    ) {
        Text(
            text = "Март ${state.currentYear}",
            color = FigmaTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActivityLegend()

        Spacer(modifier = Modifier.height(24.dp))

        when (state.activityChartTab) {
            ActivityChartTab.MONTH -> {
                MonthActivityChart(
                    year = state.currentYear,
                    month = state.currentMonth,
                    isHorizontal = true,
                    sessions = state.sessions,
                    config = ActivityChartConfig(
                        showSpacing = true,
                        itemHorizontalSpacing = 10.dp,
                        itemVerticalSpacing = 8.dp,
                        cornerRadius = 4.dp,
                        colorScheme = ActivityColorScheme.OrangeActivity
                    )
                )
            }
            ActivityChartTab.YEAR -> {
                YearActivityChart(
                    year = state.currentYear,
                    isHorizontal = true,
                    sessions = state.sessions,
                    config = ActivityChartConfig(
                        showSpacing = true,
                        itemHorizontalSpacing = 3.dp,
                        itemVerticalSpacing = 3.dp,
                        cornerRadius = 3.dp,
                        colorScheme = ActivityColorScheme.OrangeActivity
                    )
                )
            }
        }
    }
}

@Composable
private fun ActivityLegend() {
    Row {
        Text(
            text = "Меньше",
            color = FigmaTitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(10.dp))

        ActivityChartLegend(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            config = ActivityChartConfig(
                cornerRadius = 2.dp
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Больше",
            color = FigmaTitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ReadPagesOverview(
    state: ProductivityState
) {
    val periodName = when (state.activityChartTab) {
        ActivityChartTab.MONTH -> "месяц"
        ActivityChartTab.YEAR -> "год"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = FigmaRedTitle,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = "562",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "страниц прочитано за $periodName",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
private fun PreviewActivityPanelMonth() {
    ActivityPanel(
        state = ProductivityState(
            booksRead = 100,
            pagesRead = 1000,
            dayStreak = 100,
            averagePages = 12.5f,
            sessions = ProductivityPreviewData.generateMonthData()
        ),
        onToggleActivityChartSwitch = {}
    )
}

@Preview
@Composable
private fun PreviewActivityPanelYear() {
    ActivityPanel(
        state = ProductivityState(
            booksRead = 100,
            pagesRead = 1000,
            dayStreak = 100,
            averagePages = 12.5f,
            sessions = ProductivityPreviewData.generateYearData(),
            activityChartTab = ActivityChartTab.YEAR
        ),
        onToggleActivityChartSwitch = {}
    )
}
