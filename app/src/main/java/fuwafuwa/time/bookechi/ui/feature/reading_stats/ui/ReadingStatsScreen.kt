package fuwafuwa.time.bookechi.ui.feature.reading_stats.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.chart.ActivityChartConfig
import fuwafuwa.time.bookechi.base.ui.chart.ActivityColorScheme
import fuwafuwa.time.bookechi.base.ui.chart.YearQuadActivityChart
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi.ReadingStatsState
import fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi.ReadingStatsViewModel
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import fuwafuwa.time.bookechi.ui.theme.BlueMainDark
import kotlinx.serialization.Serializable

@Serializable
data object ReadingStatsScreenRoute : Screen

@Composable
fun ReadingStatsScreen(
    viewModel: ReadingStatsViewModel
) {
    val state by viewModel.model.state.collectAsState()

    ReadingStatsScreenContent(state = state)
}

@Composable
private fun ReadingStatsScreenContent(
    state: ReadingStatsState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header
            Text(
                text = "📊 Reading Stats",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueMain)
                }
            } else {
                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.MenuBook,
                        title = "Books\nCompleted",
                        value = state.totalBooksRead.toString(),
                        gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.AutoStories,
                        title = "Pages\nRead",
                        value = formatNumber(state.totalPagesRead),
                        gradientColors = listOf(Color(0xFF14B8A6), Color(0xFF06B6D4))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.LocalFireDepartment,
                        title = "Current\nStreak",
                        value = "${state.currentStreak} days",
                        gradientColors = listOf(Color(0xFFF97316), Color(0xFFEF4444))
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.TrendingUp,
                        title = "Avg. Pages\n/ Day",
                        value = String.format("%.1f", state.averagePagesPerDay),
                        gradientColors = listOf(Color(0xFF22C55E), Color(0xFF10B981))
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Year Activity Chart Section
                YearActivitySection(state)

                Spacer(modifier = Modifier.height(24.dp))

                // Monthly Activity Section
                MonthlyActivitySection(state)

                Spacer(modifier = Modifier.height(24.dp))

                // Motivation Card
                MotivationCard()
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    gradientColors: List<Color>
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(colors = gradientColors)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(28.dp)
                )
                
                Column {
                    Text(
                        text = value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun YearActivitySection(state: ReadingStatsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${state.currentYear} Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                
                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Less",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    LegendSquare(color = Color(0xFFEBEDF0))
                    LegendSquare(color = Color(0xFF9BE9A8))
                    LegendSquare(color = Color(0xFF40C463))
                    LegendSquare(color = Color(0xFF30A14E))
                    LegendSquare(color = Color(0xFF216E39))
                    Text(
                        text = "More",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            YearQuadActivityChart(
                year = state.currentYear,
                readingData = state.yearlyReadingData,
                config = ActivityChartConfig(
                    zoomMode = true,
                    showPadding = true,
                    showMonthSeparators = true,
                    zoomedItemSize = 14.dp,
                    cellSpacing = 3.dp,
                    cornerRadius = 3.dp,
                    colorScheme = ActivityColorScheme.Activity,
                    monthSeparatorColor = Color(0xFF94A3B8).copy(alpha = 0.4f),
                    separatorWidth = 1.dp
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val totalDaysRead = state.yearlyReadingData.count { it.value > 0 }
            val totalPagesThisYear = state.yearlyReadingData.values.sum()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$totalDaysRead days of reading",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "${formatNumber(totalPagesThisYear)} pages this year",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun LegendSquare(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}

@Composable
private fun MonthlyActivitySection(state: ReadingStatsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📈 This Month's Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (state.thisMonthStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reading activity this month yet.\nStart reading to see your progress!",
                        textAlign = TextAlign.Center,
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                }
            } else {
                // Simple bar visualization
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val maxPages = state.thisMonthStats.maxOfOrNull { it.totalPagesRead } ?: 1
                    
                    state.thisMonthStats.takeLast(14).forEach { stats ->
                        val heightFraction = (stats.totalPagesRead.toFloat() / maxPages).coerceIn(0.1f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightFraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(BlueMain, BlueMainDark)
                                    )
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Last ${state.thisMonthStats.takeLast(14).size} active days",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun MotivationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💡 Reading Tip",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "\"A reader lives a thousand lives before he dies. The man who never reads lives only one.\"",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "— George R.R. Martin",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000f)
        number >= 1000 -> String.format("%.1fK", number / 1000f)
        else -> number.toString()
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingStatsScreenPreview() {
    ReadingStatsScreenContent(
        state = ReadingStatsState(
            totalBooksRead = 12,
            totalPagesRead = 4523,
            currentStreak = 7,
            averagePagesPerDay = 23.5f,
            currentYear = 2026,
            yearlyReadingData = mapOf(
                "2026-01-05" to 15,
                "2026-01-10" to 25,
                "2026-01-15" to 40,
                "2026-01-20" to 30,
                "2026-01-25" to 55,
                "2026-02-03" to 20,
                "2026-02-10" to 35,
                "2026-02-20" to 45,
                "2026-03-05" to 25,
                "2026-03-10" to 60
            )
        )
    )
}
