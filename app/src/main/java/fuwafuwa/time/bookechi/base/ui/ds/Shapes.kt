package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Шкала радиусов скругления дизайн-системы Bookechi.
 * Кнопка 18 · карточка 24 · hero 28 · sheet 28 · cover 12 · chip/pill — CircleShape.
 */
object DsShapes {
    val button: Shape = RoundedCornerShape(18.dp)
    val card: Shape = RoundedCornerShape(24.dp)
    val hero: Shape = RoundedCornerShape(28.dp)
    val sheet: Shape = RoundedCornerShape(28.dp)
    val cover: Shape = RoundedCornerShape(12.dp)
    val plinth: Shape = RoundedCornerShape(16.dp)
    val tile: Shape = RoundedCornerShape(20.dp)
    val pill: Shape = CircleShape
}
