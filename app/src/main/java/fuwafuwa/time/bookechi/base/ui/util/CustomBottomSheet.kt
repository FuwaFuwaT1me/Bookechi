package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AppBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    maxHeightFraction: Float = 0.9f,
    shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    scrimColor: Color = Color.Black.copy(alpha = 0.32f),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    dragEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    var visible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }

    val animationSpec = spring<Float>(
        dampingRatio = 0.85f,
        stiffness = 300f
    )

    // размеры
    val density = LocalDensity.current
    val screenHeightPx = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    val maxSheetHeightPx = screenHeightPx * maxHeightFraction

    var sheetHeightPx by remember { mutableFloatStateOf(0f) }

    // анимация появления
    LaunchedEffect(visible) {
        progress.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = animationSpec
        )
    }

    Box(modifier.fillMaxSize()) {

        // SCRIM
        if (progress.value > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(scrimColor.copy(alpha = scrimColor.alpha * progress.value))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        scope.launch {
                            progress.animateTo(0f, animationSpec)
                            onDismiss()
                            visible = false
                        }
                    }
            )
        }

        val velocityTracker = remember { VelocityTracker() }

        // SHEET
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onSizeChanged {
                    sheetHeightPx = it.height.toFloat()
                }
                .offset {
                    IntOffset(
                        0,
                        ((1f - progress.value) * sheetHeightPx).toInt()
                    )
                }
                .then(
                    if (dragEnabled) {
                        Modifier.pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onVerticalDrag = { change, dragAmount ->
                                    velocityTracker.addPosition(
                                        change.uptimeMillis,
                                        change.position
                                    )

                                    val newValue = (progress.value - dragAmount / sheetHeightPx)
                                        .coerceIn(0f, 1f)

                                    scope.launch {
                                        progress.snapTo(newValue)
                                    }
                                },
                                onDragEnd = {
                                    val velocity = velocityTracker.calculateVelocity().y
                                    velocityTracker.resetTracking()

                                    val shouldClose = when {
                                        velocity > 1500f -> true
                                        velocity < -1500f -> false
                                        else -> progress.value < 0.5f
                                    }

                                    scope.launch {
                                        if (shouldClose) {
                                            progress.animateTo(0f, animationSpec)
                                            onDismiss()
                                            visible = false
                                        } else {
                                            progress.animateTo(1f, animationSpec)
                                        }
                                    }
                                }
                            )
                        }
                    } else Modifier
                )
                .clip(shape)
                .background(containerColor)
                .shadow(16.dp, shape)
        ) {
            // ВАЖНО: ограничение высоты + правильный скролл
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = with(density) { maxSheetHeightPx.toDp() })
            ) {
                content()
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//private fun DefaultPreview() {
//    Box {
//
//        Button(
//            onClick = {}
//        ) { }
//
//        AppBottomSheet(
//            onDismiss = {  },
//            sheetContent = {
//                Box(
//                    Modifier
//                        .fillMaxWidth()
//                        .height(400.dp)
//                        .background(Color.White)
//                ) {
//                    Text("Sheet content")
//                }
//            }
//        )
//    }
//}
