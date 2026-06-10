package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/**
 * Карточка недельной цели (объём страниц).
 * surfaceElevated + stroke: eyebrow «НА ЭТОЙ НЕДЕЛЕ» + процент-пилюля на accentSoft;
 * крупное число serif (displaySmall) + « / N стр.»; ProgressBar; caption с остатком.
 * Остаток считается из (pagesTarget - pagesRead).
 */
@Composable
fun WeeklyGoalCard(
    pagesRead: Int,
    pagesTarget: Int,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val safeTarget = pagesTarget.coerceAtLeast(1)
    val progress = (pagesRead.toFloat() / safeTarget).coerceIn(0f, 1f)
    val percent = (progress * 100).toInt()
    val remaining = (pagesTarget - pagesRead).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated, DsShapes.card)
            .border(BorderStroke(1.dp, colors.stroke), DsShapes.card)
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(text = "На этой неделе")
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelLarge,
                color = colors.accentDeep,
                modifier = Modifier
                    .background(colors.accentSoft, CircleShape)
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs + 2.dp),
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = pagesRead.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = colors.textPrimary,
            )
            Text(
                text = " / $pagesTarget стр.",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = Spacing.xs + 2.dp),
            )
        }
        ProgressBar(progress = progress)
        Text(
            text = if (remaining > 0) {
                "Осталось $remaining стр. — примерно один вечер"
            } else {
                "Цель недели выполнена"
            },
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

@Preview(name = "WeeklyGoalCard Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun WeeklyGoalCardPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            WeeklyGoalCard(pagesRead = 340, pagesTarget = 400, modifier = Modifier.padding(Spacing.lg))
        }
    }
}

@Preview(name = "WeeklyGoalCard Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun WeeklyGoalCardPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            WeeklyGoalCard(pagesRead = 340, pagesTarget = 400, modifier = Modifier.padding(Spacing.lg))
        }
    }
}
