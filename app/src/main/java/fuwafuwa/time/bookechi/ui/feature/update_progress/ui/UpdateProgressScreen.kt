package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.util.DiffConfig
import fuwafuwa.time.bookechi.base.ui.util.SimpleProgressIndicator
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressAction
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressState
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressViewModel
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellThreeActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellTwoActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaAddBookBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaFire
import fuwafuwa.time.bookechi.ui.theme.FigmaLightGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class UpdateProgressScreen(
    val book: Book
) : Screen

@Composable
fun UpdateProgressScreen(
    viewModel: UpdateProgressViewModel,
) {
    val state by viewModel.model.state.collectAsState()

    UpdateProgressScreenContent(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun UpdateProgressScreenContent(
    state: UpdateProgressState,
    onAction: (UpdateProgressAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBackground)
            .padding(horizontal = 24.dp)
            .padding(top = 36.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Где остановились\nсегодня?",
            color = FigmaTitle,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = state.book.name,
            color = FigmaSubtitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(52.dp))

        PageInput(
            state = state,
            onValueChange = {
                onAction(UpdateProgressAction.UpdatePageInput(it))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PageUpdateButtons(
            onAction = onAction
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = FigmaFire,
                        fontWeight = FontWeight.Bold
                    ),
                ) {
                    val sign = if (state.updatedInputPages > state.startPages) {
                        "+"
                    } else ""

                    append("$sign${state.updatedInputPages - state.startPages}")
                }

                append(" страниц")
            },
            color = FigmaTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = FigmaLightGrey
        )

        Spacer(modifier = Modifier.height(8.dp))

        val currentProgress = (1f * state.updatedInputPages / state.book.pages).coerceIn(0f, 1f)
        val startProgress = (1f * state.startPages / state.book.pages).coerceIn(0f, 1f)
        val currentPercent = (currentProgress * 100f).roundToInt()
        val diffPercent = ((currentProgress - startProgress) * 100f).roundToInt()
        val showDiff = state.updatedInputPages > state.startPages
        val progressLabel = if (showDiff) {
            "${currentPercent}% (+${diffPercent}%)"
        } else {
            "${currentPercent}%"
        }

        SimpleProgressIndicator(
            modifier = Modifier
                .height(24.dp)
                .fillMaxWidth()
            ,
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

        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
            ,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonColors(
                containerColor = FigmaActivityCellThreeActivity,
                contentColor = Color.White,
                disabledContainerColor = Color.White,
                disabledContentColor = FigmaAddBookBackground.copy(alpha = 0.5f),
            ),
            onClick = {

            }
        ) {
            Text(
                text = "Сохранить"
            )
        }
    }
}

@Composable
@Preview
private fun SmallProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 54,
            updatedInputPages = 62,
            book = Book(
                name = "Название книги очень длинное",
                author = "Murakami",
                coverPath = null,
                pages = 256,
                currentPage = 40
            )
        ),
        onAction = {}
    )
}

@Preview
@Composable
private fun LargeProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 54,
            updatedInputPages = 100,
            book = Book(
                name = "Название книги очень длинное",
                author = "Murakami",
                coverPath = null,
                pages = 256,
                currentPage = 40
            )
        ),
        onAction = {}
    )
}

@Preview
@Composable
private fun OutsideProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 5,
            updatedInputPages = 10,
            book = Book(
                name = "Название книги очень длинное",
                author = "Murakami",
                coverPath = null,
                pages = 256,
                currentPage = 40
            )
        ),
        onAction = {}
    )
}
