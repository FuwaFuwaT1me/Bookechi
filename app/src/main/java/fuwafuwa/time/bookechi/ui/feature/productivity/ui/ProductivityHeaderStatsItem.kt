package fuwafuwa.time.bookechi.ui.feature.productivity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.theme.FigmaProductivityHeaderItemBackground
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

@Composable
fun ProductivityHeaderStatsItem(
    numberValue: Number,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = FigmaProductivityHeaderItemBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
        ,
    ) {
        Text(
            text = "$numberValue",
            color = FigmaTitle,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = subtitle,
            color = FigmaTitle,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
private fun PrivateProductivityHeaderStatsItem() {
    ProductivityHeaderStatsItem(
        numberValue = 6,
        subtitle = "книг прочитано"
    )
}
