package fuwafuwa.time.bookechi.ui.feature.update_progress.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.UpdateProgressAction
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellFourActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellThreeActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellTwoActivity

@Composable
internal fun PageUpdateButtons(
    onAction: (UpdateProgressAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PageUpdateButton(
            buttonText = "+5",
            buttonColor = FigmaActivityCellTwoActivity,
            onClick = {
                onAction(UpdateProgressAction.UpdatePageInput(5))
            }
        )

        PageUpdateButton(
            buttonText = "+10",
            buttonColor = FigmaActivityCellThreeActivity,
            onClick = {
                onAction(UpdateProgressAction.UpdatePageInput(10))
            }
        )

        PageUpdateButton(
            buttonText = "+20",
            buttonColor = FigmaActivityCellFourActivity,
            onClick = {
                onAction(UpdateProgressAction.UpdatePageInput(20))
            }
        )
    }
}

@Composable
internal fun PageUpdateButton(
    buttonText: String,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .width(64.dp)
            .height(40.dp)
        ,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonColors(
            containerColor = buttonColor,
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = buttonColor.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Text(
            text = buttonText,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
private fun PageUpdateButtonsPreview() {
    PageUpdateButtons(onAction = {})
}
