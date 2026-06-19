package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Геометрия «бейджа-лайка», влитого в верхний правый угол обложки.
 *
 * Бейдж — квадрат [size] со скруглениями по углам:
 * - [cornerOuter] — верхний правый угол (прямой, скруглён «как угол обложки»);
 * - [cornerSmall] — верхний левый и нижний правый (небольшое скругление);
 * - [cornerInner] — нижний левый (большой полукруг, смотрящий внутрь обложки).
 *
 * [gap] — ширина «рва» между бейджем и краем гладкого выреза в обложке.
 * [edgeRound] — радиус галтели в местах, где вырез выходит на верхнюю и правую
 * кромки обложки (чтобы стыки были скруглёнными, а не острыми).
 */
data class CornerBadgeSpec(
    val size: Dp = 40.dp,
    val gap: Dp = 5.dp,
    val cornerOuter: Dp = 12.dp,
    val cornerSmall: Dp = 8.dp,
    val cornerInner: Dp = 22.dp,
    val edgeRound: Dp = 7.dp,
)

/** Форма самого бейджа — для clip/background кнопки лайка. */
fun CornerBadgeSpec.badgeShape(): RoundedCornerShape = RoundedCornerShape(
    topStart = cornerSmall,
    topEnd = cornerOuter,
    bottomEnd = cornerSmall,
    bottomStart = cornerInner,
)

/**
 * Форма обложки со скруглением [coverRadius] и гладким вырезом под [badge] в
 * верхнем правом углу.
 *
 * Контур строится явным путём (не булевой разностью), чтобы стыки выреза с
 * верхней и правой кромками обложки были скруглёнными галтелями
 * [CornerBadgeSpec.edgeRound], а не острыми «зубцами». «Ров» вокруг бейджа имеет
 * ширину [CornerBadgeSpec.gap]; большой вогнутый изгиб повторяет нижний-левый
 * полукруг бейджа.
 */
class CoverCornerCutoutShape(
    private val coverRadius: Dp,
    private val badge: CornerBadgeSpec,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val w = size.width
        val h = size.height
        val r = with(density) { coverRadius.toPx() }
        val s = with(density) { badge.size.toPx() }
        val g = with(density) { badge.gap.toPx() }
        val cInner = with(density) { badge.cornerInner.toPx() }
        val fr = with(density) { badge.edgeRound.toPx() }

        val xMoatLeft = w - s - g          // левая граница рва
        val yMoatBottom = s + g            // нижняя граница рва
        val aBig = cInner + g              // радиус большой вогнутой дуги
        val bigCx = xMoatLeft + aBig       // центр большой дуги X
        val bigCy = yMoatBottom - aBig     // центр большой дуги Y

        val path = Path().apply {
            // Старт: верхняя кромка после левого-верхнего скругления.
            moveTo(r, 0f)
            // Верхняя кромка до галтели входа в вырез.
            lineTo(xMoatLeft - fr, 0f)
            // Галтель: верхняя кромка плавно загибается вниз в ров.
            quadraticTo(xMoatLeft, 0f, xMoatLeft, fr)
            // Левая стенка рва вниз до большой дуги.
            lineTo(xMoatLeft, bigCy)
            // Большой вогнутый изгиб (обходим нижний-левый полукруг бейджа).
            arcTo(
                rect = Rect(bigCx - aBig, bigCy - aBig, bigCx + aBig, bigCy + aBig),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -90f,
                forceMoveTo = false,
            )
            // Нижняя стенка рва вправо до галтели выхода на правую кромку.
            lineTo(w - fr, yMoatBottom)
            // Галтель: нижняя стенка рва плавно загибается вверх на правую кромку.
            quadraticTo(w, yMoatBottom, w, yMoatBottom + fr)
            // Правая кромка вниз до правого-нижнего скругления.
            lineTo(w, h - r)
            arcTo(
                rect = Rect(w - 2 * r, h - 2 * r, w, h),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
            // Нижняя кромка влево до левого-нижнего скругления.
            lineTo(r, h)
            arcTo(
                rect = Rect(0f, h - 2 * r, 2 * r, h),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
            // Левая кромка вверх до левого-верхнего скругления.
            lineTo(0f, r)
            arcTo(
                rect = Rect(0f, 0f, 2 * r, 2 * r),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false,
            )
            close()
        }
        return Outline.Generic(path)
    }
}
