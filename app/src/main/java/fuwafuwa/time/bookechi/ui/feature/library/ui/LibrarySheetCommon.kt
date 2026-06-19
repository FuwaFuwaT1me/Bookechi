package fuwafuwa.time.bookechi.ui.feature.library.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.base.ui.ds.BookCover
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.base.ui.ds.FilterChip
import fuwafuwa.time.bookechi.base.ui.ds.Spacing
import fuwafuwa.time.bookechi.data.model.ReadingStatus
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.coroutines.launch

/** Три статуса, доступных при добавлении/редактировании. */
internal val EditableStatuses: List<Pair<ReadingStatus, Int>> = listOf(
    ReadingStatus.Reading to R.string.lib_status_reading,
    ReadingStatus.Planned to R.string.lib_status_planned,
    ReadingStatus.Completed to R.string.lib_status_completed,
)

private val CoverSlotWidth = 132.dp
private val CameraButtonSize = 48.dp

/**
 * Зона выбора обложки: тёплая панель на всю ширину, по центру — обложка книги
 * (или пунктирный плейсхолдер с иконкой книги), а в правом нижнем углу обложки —
 * круглая терракотовая кнопка-камера. Подпись «Добавьте обложку…» под панелью.
 */
@Composable
internal fun CoverTile(
    coverPath: String?,
    title: String,
    author: String,
    isLoading: Boolean,
    onPick: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    // Вертикальный градиент: тёплый персик сверху → светлый крем снизу.
    val panelBrush = Brush.verticalGradient(
        listOf(colors.accentSoft, lerp(colors.cardTint, Color.White, 0.4f)),
    )

    // Клик по всей панели + анимация: на каждый тап обложка с кружком целиком
    // проигрывают сжатие → возврат (а не только пока зажато).
    val interaction = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val coverScale = remember { Animatable(1f) }
    val onCoverClick: () -> Unit = {
        scope.launch {
            coverScale.animateTo(0.94f, animationSpec = tween(durationMillis = 90))
            coverScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            )
        }
        onPick()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DsShapes.tile)
                .background(panelBrush)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onCoverClick,
                )
                .padding(vertical = Spacing.xxl),
            contentAlignment = Alignment.Center,
        ) {
            // Обложка/плейсхолдер + кнопка-камера, прижатая к нижнему правому углу.
            // Масштабируются вместе при клике по панели.
            Box(
                modifier = Modifier.scale(coverScale.value),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    if (coverPath != null) {
                        BookCover(
                            coverPath = coverPath,
                            title = title.ifBlank { stringResource(R.string.lib_cover) },
                            author = author.ifBlank { "" },
                            width = CoverSlotWidth,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(CoverSlotWidth)
                                .aspectRatio(2f / 3f)
                                .clip(DsShapes.cover)
                                .background(colors.surface)
                                .dashedBorder(
                                    color = colors.textSecondary.copy(alpha = 0.4f),
                                    cornerRadius = 12.dp,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.dp,
                            color = colors.accent,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(x = Spacing.sm, y = Spacing.sm)
                        .size(CameraButtonSize)
                        .clip(CircleShape)
                        .background(colors.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = stringResource(R.string.lib_cover_pick),
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = stringResource(R.string.lib_cover_caption),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )

        if (coverPath != null && onClear != null) {
            Text(
                text = stringResource(R.string.lib_cover_remove),
                style = MaterialTheme.typography.labelMedium,
                color = colors.accentDeep,
                modifier = Modifier
                    .padding(top = Spacing.xs)
                    .clickable(onClick = onClear),
            )
        }
    }
}

/** Пунктирная рамка скруглённого прямоугольника. */
private fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp = 1.5.dp,
): Modifier = this.drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
        style = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
        ),
    )
}

/** Ряд из трёх статус-чипов (DS FilterChip). */
@Composable
internal fun StatusChipsRow(
    selected: ReadingStatus,
    onSelect: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        EditableStatuses.forEach { (status, labelRes) ->
            FilterChip(
                text = stringResource(labelRes),
                selected = selected == status,
                onClick = { onSelect(status) },
            )
        }
    }
}

/** Мягкое сообщение об ошибке под полем (accentDeep). */
@Composable
internal fun FieldError(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = BookechiTheme.colors.accentDeep,
        modifier = modifier.padding(start = Spacing.xs, top = Spacing.xs),
    )
}
