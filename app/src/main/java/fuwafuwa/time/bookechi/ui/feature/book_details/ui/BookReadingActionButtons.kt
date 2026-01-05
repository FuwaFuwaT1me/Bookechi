package fuwafuwa.time.bookechi.ui.feature.book_details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.BookDetailsAction
import fuwafuwa.time.bookechi.ui.theme.BlueMain

private val OrangeButton = Color(0xFFFF9800)
private val RedButton = Color(0xFFE53935)
private val GreenButton = Color(0xFF4CAF50)

@Composable
fun BookReadingActionButtons(
    readingStatus: ReadingStatus,
    onAction: (BookDetailsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (readingStatus) {
        ReadingStatus.None -> {
            ActionButton(
                text = "Начать читать",
                color = BlueMain,
                onClick = { onAction(BookDetailsAction.StartReading) },
                modifier = modifier
            )
        }

        ReadingStatus.Reading -> {
            ReadingInProgressButtons(
                onAction = onAction,
                modifier = modifier
            )
        }

        ReadingStatus.Paused -> {
            ActionButton(
                text = "Возобновить чтение",
                color = GreenButton,
                onClick = { onAction(BookDetailsAction.ResumeReading) },
                modifier = modifier
            )
        }

        ReadingStatus.Stopped -> {
            ActionButton(
                text = "Начать читать снова",
                color = BlueMain,
                onClick = { onAction(BookDetailsAction.StartReadingAgain) },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        onClick = onClick
    ) {
        Text(text)
    }
}

@Composable
private fun ReadingInProgressButtons(
    onAction: (BookDetailsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonColors(
                containerColor = OrangeButton,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            onClick = { onAction(BookDetailsAction.PauseReading) }
        ) {
            Text("Приостановить")
        }

        Button(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonColors(
                containerColor = RedButton,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            onClick = { onAction(BookDetailsAction.FinishReading) }
        ) {
            Text("Закончить")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookReadingActionButtonsNonePreview() {
    BookReadingActionButtons(
        readingStatus = ReadingStatus.None,
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookReadingActionButtonsReadingPreview() {
    BookReadingActionButtons(
        readingStatus = ReadingStatus.Reading,
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookReadingActionButtonsPausedPreview() {
    BookReadingActionButtons(
        readingStatus = ReadingStatus.Paused,
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BookReadingActionButtonsStoppedPreview() {
    BookReadingActionButtons(
        readingStatus = ReadingStatus.Stopped,
        onAction = {}
    )
}
