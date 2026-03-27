@file:OptIn(ExperimentalMaterial3Api::class)

package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun BoxScope.BookechiBottomSheetScaffold(
    onDismissRequest: () -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentPadding: PaddingValues = PaddingValues(
        start = 20.dp,
        end = 20.dp,
        top = 12.dp,
        bottom = 24.dp
    ),
    content: LazyListScope.() -> Unit
) {
    val blockParentSheetScroll = rememberBlockParentSheetScroll()
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    fun dismissSheet() {
        scope.launch {
            sheetState.hide()
            onDismissRequest()
        }
    }

    val screenHeightPx = LocalWindowInfo.current.containerSize.height
        .toFloat()
        .coerceAtLeast(1f)

    val scrimAlpha by remember(sheetState, screenHeightPx) {
        derivedStateOf {
            val offsetPx = runCatching { sheetState.requireOffset() }
                .getOrDefault(screenHeightPx)

            val progress = (1f - (offsetPx / screenHeightPx))
                .coerceIn(0f, 1f)

            0.5f * progress
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (scrimAlpha > 0.05f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { dismissSheet() })
                    }
            )
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { dismissSheet() },
            containerColor = containerColor,
            scrimColor = Color.Transparent,
            dragHandle = {
                BottomSheetDefaults.DragHandle()
            }
        ) {
            header()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .nestedScroll(blockParentSheetScroll),
                contentPadding = contentPadding,
                content = content
            )
        }
    }
}
