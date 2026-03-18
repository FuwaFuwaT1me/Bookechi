package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
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
import fuwafuwa.time.bookechi.ui.theme.FigmaBookCover
import fuwafuwa.time.bookechi.ui.theme.FigmaFire
import fuwafuwa.time.bookechi.ui.theme.FigmaGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaLightGrey
import fuwafuwa.time.bookechi.ui.theme.FigmaStreakBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle
import kotlinx.serialization.Serializable
import kotlin.math.abs
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
        Header(state, onAction)

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

        Spacer(modifier = Modifier.height(36.dp))

        PageInput(
            state = state,
            onValueChange = {
                onAction(UpdateProgressAction.UpdatePageInput(it))
            }
        )

//        Spacer(modifier = Modifier.height(12.dp))

//        PageUpdateButtons(
//            onAction = onAction
//        )
//
        Spacer(Modifier.height(32.dp))

        Text(
            text = buildAnnotatedString {
                if (state.updatedInputPages > state.startPages) {
                    withStyle(
                        style = SpanStyle(
                            color = FigmaFire,
                            fontWeight = FontWeight.Bold
                        ),
                    ) {
                        append("+${state.updatedInputPages - state.startPages}")
                    }

                    append(" страниц\n")
                } else if (state.updatedInputPages == -1 || state.updatedInputPages == state.startPages) {
                    // do nothing
                } else {
                    append("Вы вернулись на ")

                    withStyle(
                        style = SpanStyle(
                            color = FigmaFire,
                            fontWeight = FontWeight.Bold
                        ),
                    ) {
                        append("${abs(state.startPages - state.updatedInputPages)}")
                    }

                    append(" страниц")
                }
            },
            color = FigmaTitle,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun Header(
    state: UpdateProgressState,
    onAction: (UpdateProgressAction) -> Unit,
) {
    Row {

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
            ,
            colors = IconButtonColors(
                containerColor = FigmaBookCover,
                contentColor = Color.White,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            shape = CircleShape,
            onClick = {
                onAction(UpdateProgressAction.NavigateBack)
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        if (state.updatedInputPages != state.startPages && state.updatedInputPages != -1) {
            Spacer(Modifier.width(16.dp))

            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                ,
                colors = IconButtonColors(
                    containerColor = FigmaFire,
                    contentColor = Color.White,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = CircleShape,
                onClick = {
                    onAction(UpdateProgressAction.SaveChanges(state.updatedInputPages))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Apply",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
@Preview
private fun PositiveProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 54,
            updatedInputPages = 62,
            book = Book(
                name = "Название книги очень длинное",
                author = "Murakami",
                coverPath = null,
                pages = 256,
                currentPage = 40,
                isFavorite = false,
            )
        ),
        onAction = {}
    )
}

@Preview
@Composable
private fun NegativeProgressPreview() {
    UpdateProgressScreenContent(
        state = UpdateProgressState(
            startPages = 54,
            updatedInputPages = 26,
            book = Book(
                name = "Название книги очень длинное",
                author = "Murakami",
                coverPath = null,
                pages = 256,
                currentPage = 40,
                isFavorite = false,
            )
        ),
        onAction = {}
    )
}
