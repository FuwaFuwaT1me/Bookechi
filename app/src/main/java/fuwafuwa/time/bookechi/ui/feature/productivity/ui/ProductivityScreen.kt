package fuwafuwa.time.bookechi.ui.feature.productivity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.BuildConfig
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ActivityChartTab
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityAction
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityViewModel
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart.ActivityPanel
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Serializable
data object ProductivityScreen : Screen

@Composable
fun ProductivityScreen(
    viewModel: ProductivityViewModel
) {
    val state by viewModel.model.state.collectAsState()

    ProductivityScreenPrivate(
        state = state,
        onToggleActivityChartSwitch = { tab ->
            ActivityChartTab.fromIndex(tab)?.let {
                viewModel.sendAction(
                    ProductivityAction.ToggleActivityChartTab(it)
                )
            }
        },
        debugActions = if (BuildConfig.DEBUG) {
            ProductivityDebugActions(
                overwriteYear = { year, pagesPerDay, booksCount ->
                    viewModel.sendAction(
                        ProductivityAction.DebugOverwriteYear(year, pagesPerDay, booksCount)
                    )
                },
                overwriteMonth = { year, month, pagesPerDay, booksCount ->
                    viewModel.sendAction(
                        ProductivityAction.DebugOverwriteMonth(year, month, pagesPerDay, booksCount)
                    )
                },
                clearAll = {
                    viewModel.sendAction(ProductivityAction.DebugClearAll)
                }
            )
        } else {
            null
        }
    )
}

private data class ProductivityDebugActions(
    val overwriteYear: (Int, Int, Int) -> Unit,
    val overwriteMonth: (Int, Int, Int, Int) -> Unit,
    val clearAll: () -> Unit
)

@Composable
private fun ProductivityScreenPrivate(
    state: ProductivityState,
    onToggleActivityChartSwitch: (Int) -> Unit,
    debugActions: ProductivityDebugActions? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Header(state)

        Spacer(modifier = Modifier.height(30.dp))

        ActivityPanel(state, onToggleActivityChartSwitch)

        if (debugActions != null) {
            Spacer(modifier = Modifier.height(24.dp))
            DebugPanel(
                onOverwriteYear = debugActions.overwriteYear,
                onOverwriteMonth = debugActions.overwriteMonth,
                onClearAll = debugActions.clearAll
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun Header(
    state: ProductivityState
) {
    Column {
        Text(
            text = "Моя продуктивность",
            color = FigmaTitle,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(26.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProductivityHeaderStatsItem(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    ,
                    numberValue = state.booksRead,
                    subtitle = "книг прочитано"
                )

                ProductivityHeaderStatsItem(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    ,
                    numberValue = state.pagesRead,
                    subtitle = "страниц прочитано"
                )
            }

            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProductivityHeaderStatsItem(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    ,
                    numberValue = state.dayStreak,
                    subtitle = "дней без перерывов"
                )

                ProductivityHeaderStatsItem(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    ,
                    numberValue = BigDecimal(
                        state.averagePages.toString()
                    ).setScale(1, RoundingMode.HALF_UP),
                    subtitle = "страниц/день в среднем"
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProductivityScreenMonth() {
    ProductivityScreenPrivate(
        state = ProductivityState(
            booksRead = 6,
            pagesRead = 18574,
            dayStreak = 280,
            averagePages = 12.5f,
            sessions = ProductivityPreviewData.generateMonthData()
        ),
        onToggleActivityChartSwitch = {},
        debugActions = null
    )
}

@Preview
@Composable
private fun PreviewProductivityScreenYear() {
    ProductivityScreenPrivate(
        state = ProductivityState(
            booksRead = 6,
            pagesRead = 18574,
            dayStreak = 280,
            averagePages = 12.5f,
            sessions = ProductivityPreviewData.generateYearData(),
            activityChartTab = ActivityChartTab.YEAR
        ),
        onToggleActivityChartSwitch = {},
        debugActions = null
    )
}

@Composable
private fun DebugPanel(
    onOverwriteYear: (Int, Int, Int) -> Unit,
    onOverwriteMonth: (Int, Int, Int, Int) -> Unit,
    onClearAll: () -> Unit
) {
    val today = remember { LocalDate.now() }
    var yearText by remember { mutableStateOf(today.year.toString()) }
    var monthText by remember { mutableStateOf(today.monthValue.toString()) }
    var pagesText by remember { mutableStateOf("30") }
    var booksText by remember { mutableStateOf("3") }

    val year = yearText.toIntOrNull() ?: today.year
    val month = (monthText.toIntOrNull() ?: today.monthValue).coerceIn(1, 12)
    val pages = pagesText.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val books = booksText.toIntOrNull()?.coerceAtLeast(1) ?: 1

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F3F1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Debug panel (overwrite data)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { yearText = it },
                    label = { Text("Year") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = monthText,
                    onValueChange = { monthText = it },
                    label = { Text("Month") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = pagesText,
                    onValueChange = { pagesText = it },
                    label = { Text("Pages/Day") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = booksText,
                    onValueChange = { booksText = it },
                    label = { Text("Books") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onOverwriteYear(year, pages, books) }) {
                    Text("Overwrite Year")
                }
                Button(onClick = { onOverwriteMonth(year, month, pages, books) }) {
                    Text("Overwrite Month")
                }
            }

            Button(onClick = onClearAll) {
                Text("Clear Books + Sessions")
            }
        }
    }
}
