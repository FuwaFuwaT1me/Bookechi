package fuwafuwa.time.bookechi.ui.feature.productivity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityState
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.ProductivityViewModel
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.activity_chart.ActivityPanel
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
import kotlinx.serialization.Serializable

@Serializable
data object ProductivityScreen : Screen

@Composable
fun ProductivityScreen(
    viewModel: ProductivityViewModel
) {
    val state by viewModel.model.state.collectAsState()

    ProductivityScreenPrivate(
        state = state,
    )
}

@Composable
private fun ProductivityScreenPrivate(
    state: ProductivityState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {

        Header(state)

        Spacer(modifier = Modifier.height(30.dp))

        ActivityPanel(state)
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
                    numberValue = state.averagePages,
                    subtitle = "страниц/день в среднем"
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProductivityScreen() {
    ProductivityScreenPrivate(
        state = ProductivityState(
            booksRead = 6,
            pagesRead = 18574,
            dayStreak = 280,
            averagePages = 12.5f,
            sessions = ProductivityPreviewData.generateMonthData()
        )
    )
}
