package fuwafuwa.time.bookechi.ui.feature.update_result.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.util.DiffConfig
import fuwafuwa.time.bookechi.base.ui.util.SimpleProgressIndicator
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultAction
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultState
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.UpdateResultViewModel
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellThreeActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellTwoActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaFire
import fuwafuwa.time.bookechi.ui.theme.FigmaLightGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
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
    val delta = state.pagesDelta
    val absPages = abs(delta)
    val sign = if (delta >= 0) "+" else "−"
    val pagesLabel = pluralizePages(absPages)
    val flameColor = Color(0xFFFFE7D2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = FigmaFire
            )
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fire_5),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(flameColor)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                ) {
                    Text(
                        text = "${state.newStreakCount}",
                        color = FigmaTitle,
                        fontSize = 84.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$sign$absPages",
                    color = Color.White,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = pagesLabel,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))

            val currentProgress = (1f * state.updatedPages / state.allBookPages).coerceIn(0f, 1f)
            val startProgress = (1f * state.startPages / state.allBookPages).coerceIn(0f, 1f)
            val currentPercent = (currentProgress * 100f).roundToInt()
            val diffPercent = ((currentProgress - startProgress) * 100f).roundToInt()
            val showDiff = state.updatedPages > state.startPages
            val progressLabel = if (showDiff) {
                "+${diffPercent}%"
            } else {
                "${currentPercent}%"
            }

            Column(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 16.dp),
            ) {

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = FigmaTitle,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            ),
                        ) {
                            append("${100 * state.startPages / state.allBookPages}%")
                        }

                        withStyle(
                            style = SpanStyle(
                                baselineShift = BaselineShift(0.15f)
                            ),
                        ) {
                            append("  ➞  ")
                        }

                        withStyle(
                            style = SpanStyle(
                                color = FigmaFire,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                        ) {
                            append("${100 * state.updatedPages / state.allBookPages}%")
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                SimpleProgressIndicator(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(),
                    progress = currentProgress,
                    progressBarColor = FigmaFire,
                    cornerRadius = 6.dp,
                    trackColor = FigmaLightGrey,
                    diffConfig = DiffConfig(
                        lastProgress = if (showDiff) startProgress else currentProgress,
                        diffColor = FigmaActivityCellTwoActivity,
                        labelText = progressLabel,
                        showingPercentColor = Color.White,
                        showPercent = true
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Так держать!",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ты здорово постарался!",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonColors(
                    containerColor = FigmaTitle,
                    contentColor = Color.White,
                    disabledContainerColor = FigmaTitle.copy(alpha = 0.6f),
                    disabledContentColor = Color.White
                ),
                onClick = { onAction(UpdateResultAction.Done) }
            ) {
                Text(
                    text = "Готово",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun pluralizePages(pages: Int): String {
    val mod100 = pages % 100
    val mod10 = pages % 10

    return when {
        mod100 in 11..14 -> "страниц"
        mod10 == 1 -> "страница"
        mod10 in 2..4 -> "страницы"
        else -> "страниц"
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdateResultScreenPreview() {
    UpdateResultScreenContent(
        state = UpdateResultState(
            pagesDelta = 15,
            startPages = 54,
            updatedPages = 15 + 54,
            allBookPages = 256,
            newStreakCount = 5,
        ),
        onAction = {}
    )
}
