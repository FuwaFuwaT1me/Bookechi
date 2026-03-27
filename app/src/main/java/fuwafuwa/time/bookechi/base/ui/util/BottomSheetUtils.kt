package fuwafuwa.time.bookechi.base.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

@Composable
fun rememberBlockParentSheetScroll(): NestedScrollConnection {
    return remember {
        object : NestedScrollConnection {

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Съедаем весь оставшийся вертикальный скролл,
                // который внутренний контент уже не смог обработать.
                return Offset(x = 0f, y = available.y)
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                // Съедаем остаточный fling, чтобы он не ушел в sheet.
                return Velocity(x = 0f, y = available.y)
            }
        }
    }
}
