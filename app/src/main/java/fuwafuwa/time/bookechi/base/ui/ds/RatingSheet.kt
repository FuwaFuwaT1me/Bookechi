package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Шторка проставления оценки книге (1–5). Звёзды анимированы: тапнутая
 * «заворачивается» (360° + сжатие→пружина), остальные слева подпрыгивают каскадом.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingSheet(
    current: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    onClear: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draft by remember { mutableIntStateOf(current) }
    var lastTapped by remember { mutableIntStateOf(0) }
    var tapNonce by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.canvas,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.shelf_rate_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.lg))
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                (1..5).forEach { n ->
                    RatingStar(
                        index = n,
                        filled = n <= draft,
                        isTapped = n == lastTapped,
                        animKey = tapNonce,
                        onTap = {
                            draft = n
                            lastTapped = n
                            tapNonce++
                        },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xl))
            PrimaryButton(
                text = stringResource(R.string.log_save),
                onClick = { onSave(draft) },
                enabled = draft > 0,
            )
            Spacer(Modifier.height(Spacing.sm))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClear() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.shelf_rating_clear),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun RatingStar(
    index: Int,
    filled: Boolean,
    isTapped: Boolean,
    animKey: Int,
    onTap: (Int) -> Unit,
) {
    val colors = BookechiTheme.colors
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(animKey) {
        if (animKey == 0) return@LaunchedEffect
        if (!filled) {
            scale.animateTo(1f, tween(150))
            return@LaunchedEffect
        }
        if (isTapped) {
            launch {
                rotation.snapTo(0f)
                rotation.animateTo(360f, tween(460, easing = FastOutSlowInEasing))
                rotation.snapTo(0f)
            }
            scale.snapTo(1f)
            scale.animateTo(0.5f, tween(110))
            scale.animateTo(1f, spring(dampingRatio = 0.34f, stiffness = Spring.StiffnessMediumLow))
        } else {
            delay(index * 45L)
            scale.animateTo(1.18f, tween(120))
            scale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
    }

    Icon(
        imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarBorder,
        contentDescription = null,
        tint = if (filled) colors.accent else colors.textSecondary.copy(alpha = 0.4f),
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = rotation.value
            }
            .clip(CircleShape)
            .clickable { onTap(index) }
            .padding(2.dp),
    )
}
