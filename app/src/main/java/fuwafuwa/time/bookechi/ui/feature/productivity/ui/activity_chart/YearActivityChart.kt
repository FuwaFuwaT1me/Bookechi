package fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.getRelativeActivityIntensity
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityPreviewData
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import java.time.LocalDate

private const val MONTHS_IN_YEAR = 12

/** Краткие 3-буквенные подписи месяцев под столбиками. */
private val MONTH_SHORT = listOf(
    "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
    "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек",
)

private val BAR_AREA_HEIGHT = 130.dp

/**
 * Годовой график — вертикальные столбики по месяцам (макет «06 Продуктивность — год»).
 *
 * Один градиентный столбик на месяц (Янв..Дек), высота ∝ суммарным страницам месяца,
 * слабо скруглённый верх, широкий. Месяц-пик подписан значением сверху. Текущий месяц —
 * выделение «цвет → белая прослойка → чёрная обводка». Под столбиками — краткое имя месяца.
 */
@Composable
fun YearActivityChart(
    year: Int,
    modifier: Modifier = Modifier,
    config: ActivityChartConfig = ActivityChartConfig(cornerRadius = 2.dp),
    sessions: List<DailyReadingStats> = emptyList(),
) {
    val colors = BookechiTheme.colors
    val pagesByMonth = remember(year, sessions) { buildMonthlyPages(year, sessions) }
    val maxPages = pagesByMonth.maxOrNull() ?: 0
    val today = LocalDate.now()
    val currentMonth = if (today.year == year) today.monthValue else 0

    val barShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        letterSpacing = 0.sp,
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.Bottom,
    ) {
        for (month in 1..MONTHS_IN_YEAR) {
            val pages = pagesByMonth[month - 1]
            val isPeak = maxPages > 0 && pages == maxPages
            val isCurrent = month == currentMonth
            val fraction = if (maxPages > 0) pages.toFloat() / maxPages else 0f

            // Цвет столбика зависит от активности месяца: чем больше страниц — тем насыщеннее.
            val intensity = getRelativeActivityIntensity(pages, maxPages).ordinal
            val barBrush = Brush.verticalGradient(
                listOf(
                    colors.heatmap[intensity],
                    colors.heatmap[(intensity + 1).coerceAtMost(colors.heatmap.lastIndex)],
                )
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Слот под значение пика — зарезервирован у всех, чтобы столбики стояли на одной базе.
                Text(
                    text = if (isPeak) formatPages(pages) else "",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.accentDeep,
                    maxLines = 1,
                    modifier = Modifier.height(16.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Spacing.xs))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BAR_AREA_HEIGHT),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    val barHeight = when {
                        pages <= 0 -> 4.dp
                        else -> (BAR_AREA_HEIGHT.value * fraction).dp.coerceAtLeast(8.dp)
                    }
                    val barModifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(barHeight)

                    when {
                        pages <= 0 -> Box(
                            modifier = barModifier.clip(barShape).background(colors.stroke),
                        )
                        isCurrent -> {
                            // Текущий месяц: асимметричная обводка — низ слегка скруглён,
                            // верх элегантно крупнее. Цвет → белая прослойка → чёрная обводка.
                            val highlightShape = RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 3.dp,
                                bottomEnd = 3.dp,
                            )
                            Box(
                                modifier = barModifier
                                    .border(1.dp, colors.textPrimary, highlightShape)
                                    .padding(3.5.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(highlightShape)
                                        .background(barBrush),
                                )
                            }
                        }
                        else -> Box(
                            modifier = barModifier.clip(barShape).background(barBrush),
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.xs))

                Text(
                    text = MONTH_SHORT[month - 1],
                    style = if (isCurrent) {
                        labelStyle.copy(fontWeight = FontWeight.Bold)
                    } else {
                        labelStyle
                    },
                    color = if (isCurrent) colors.textPrimary else colors.textSecondary,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Суммарные страницы по каждому месяцу указанного года. */
private fun buildMonthlyPages(
    year: Int,
    sessions: List<DailyReadingStats>,
): IntArray {
    val pagesByMonth = IntArray(MONTHS_IN_YEAR)
    sessions.forEach { session ->
        val date = session.localDate
        if (date.year == year) {
            pagesByMonth[date.monthValue - 1] += session.totalPagesRead
        }
    }
    return pagesByMonth
}

/** «1207» -> «1 207» (разряды тысяч пробелом). */
private fun formatPages(n: Int): String =
    n.toString().reversed().chunked(3).joinToString(" ").reversed()

@Preview(name = "YearActivityChart Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun YearActivityChartPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                YearActivityChart(
                    year = 2026,
                    sessions = ProductivityPreviewData.generateYearData(year = 2026),
                )
            }
        }
    }
}

@Preview(name = "YearActivityChart Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun YearActivityChartPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                YearActivityChart(
                    year = 2026,
                    sessions = ProductivityPreviewData.generateYearData(year = 2026),
                )
            }
        }
    }
}
