package fuwafuwa.time.bookechi.ui.feature.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlin.math.PI
import kotlin.math.sin
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.library.ui.LibraryScreen
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityScreen
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Тёплая густая тень под плавающим нижним баром (espresso). */
private val BottomBarShadow = Color(0xFF2A1E15)

/** Тип иконки вкладки — рисуется сплошной заливкой currentColor (см. [NavGlyph]). */
enum class NavGlyph { HOME, STATS, LIBRARY }

data class BottomNavItem(
    @StringRes val labelRes: Int,
    val glyph: NavGlyph,
    val route: Screen,
)

val BottomNavItems = listOf(
    BottomNavItem(R.string.ds_nav_activity, NavGlyph.HOME, BookListScreen),
    BottomNavItem(R.string.ds_nav_productivity, NavGlyph.STATS, ProductivityScreen),
    BottomNavItem(R.string.ds_nav_library, NavGlyph.LIBRARY, LibraryScreen),
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val barShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 22.dp,
                shape = barShape,
                clip = false,
                ambientColor = BottomBarShadow,
                spotColor = BottomBarShadow,
            )
            .clip(barShape)
            .background(colors.surface)
            .border(1.dp, colors.stroke, barShape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavItems.forEach { item ->
                val selected = currentRoute == item.route::class.qualifiedName
                NavItem(
                    modifier = Modifier.weight(1f),
                    selected = selected,
                    label = stringResource(item.labelRes),
                    glyph = item.glyph,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    selected: Boolean,
    label: String,
    glyph: NavGlyph,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(),
        label = "navItemScale",
    )
    val tint by animateColorAsState(
        targetValue = if (selected) colors.accentDeep else colors.textSecondary.copy(alpha = 0.88f),
        animationSpec = tween(220),
        label = "navItemTint",
    )

    // Прогресс «оживления» иконки: 1f — покой, при выборе вкладки проигрывается 0→1.
    // Каждый глиф трактует этот прогресс по-своему (см. drawNavGlyph).
    val anim = remember { Animatable(1f) }
    LaunchedEffect(selected) {
        if (selected) {
            anim.snapTo(0f)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = when (glyph) {
                    NavGlyph.HOME -> tween(durationMillis = 480, easing = LinearEasing)
                    NavGlyph.STATS -> tween(durationMillis = 720, easing = LinearEasing)
                    NavGlyph.LIBRARY -> tween(durationMillis = 600, easing = LinearEasing)
                },
            )
        }
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        // Активный индикатор — скруглённо-квадратная «таблетка» за иконкой.
        if (selected) {
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.accentSoft),
            )
        }
        Canvas(
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    // Книга «разворачивается»: один плавный мах вокруг вертикали и
                    // возврат в покой. Импульс начинается и заканчивается на 0° —
                    // нет резкого скачка из исходного состояния в начале (это и было
                    // причиной «дёрганья»: раньше anim=0 был свёрнутым кадром).
                    if (glyph == NavGlyph.LIBRARY) {
                        rotationY = sin(anim.value * PI).toFloat() * -58f
                        cameraDistance = 14f * density
                    }
                },
        ) {
            drawNavGlyph(glyph, tint, anim.value)
        }
    }
}

// Точные пути иконок (сетка 24×24), залитые fill. Парсятся один раз.
private val HomeGlyphPath = PathParser().parsePathString(
    "M12 3.1c-.4 0-.8.15-1.1.4l-7.5 6.4c-.5.4-.7 1-.5 1.6.2.6.7 1 1.4 1h.5v5.9c0 1 " +
        ".8 1.8 1.8 1.8h2.5v-4.6c0-.8.6-1.4 1.4-1.4h3c.8 0 1.4.6 1.4 1.4v4.6h2.5c1 0 " +
        "1.8-.8 1.8-1.8v-5.9h.5c.7 0 1.2-.4 1.4-1 .2-.6 0-1.2-.5-1.6l-7.5-6.4c-.3-.25-.7-.4-1.1-.4Z",
).toPath()
private val LibraryGlyphPath1 = PathParser().parsePathString(
    "M11.2 6.6C9.4 5.1 6.9 4.3 4.3 4.3c-.5 0-1 .2-1.35.55-.35.35-.55.8-.55 1.3v9.9c0 1 " +
        ".8 1.75 1.8 1.75 2.4 0 4.55.5 6.3 1.7.25.18.55-.02.55-.32V7.4c0-.3-.13-.58-.35-.78Z",
).toPath()
private val LibraryGlyphPath2 = PathParser().parsePathString(
    "M12.8 6.6c1.8-1.5 4.3-2.3 6.9-2.3.5 0 1 .2 1.35.55.35.35.55.8.55 1.3v9.9c0 1-.8 " +
        "1.75-1.8 1.75-2.4 0-4.55.5-6.3 1.7-.25.18-.55-.02-.55-.32V7.4c0-.3.13-.58.35-.78Z",
).toPath()

/**
 * Рисует сплошную (залитую) иконку вкладки на сетке 24, отмасштабированной под Canvas.
 *
 * [anim] — прогресс select-анимации (1f — покой). Глифы анимируются так:
 *  - HOME    — подскок с лёгким squash-and-stretch;
 *  - STATS   — столбики по очереди приседают и встают (волна);
 *  - LIBRARY — две страницы распахиваются вокруг корешка.
 */
private fun DrawScope.drawNavGlyph(glyph: NavGlyph, color: Color, anim: Float) {
    val s = size.minDimension / 24f
    scale(s, s, pivot = Offset.Zero) {
        when (glyph) {
            NavGlyph.HOME -> {
                val arc = sin(anim * PI).toFloat() // 0 по краям, 1 в верхней точке прыжка
                val dy = -3.2f * arc               // подъём вверх
                val sx = 1f - 0.05f * arc          // вытягивание по вертикали в апексе
                val sy = 1f + 0.08f * arc
                translate(0f, dy) {
                    scale(sx, sy, pivot = Offset(12f, 21f)) {
                        drawPath(HomeGlyphPath, color)
                    }
                }
            }
            NavGlyph.STATS -> {
                // Три скруглённых столбика разной высоты, без осей.
                val r = CornerRadius(2.3f, 2.3f)
                val baseline = 20.2f
                // x, полная высота — нижняя грань у всех на baseline.
                val bars = listOf(
                    3.8f to 7.6f,
                    9.7f to 16.4f,
                    15.6f to 11.4f,
                )
                bars.forEachIndexed { i, (x, fullH) ->
                    // Окно приседания смещено по столбикам — получается «волна».
                    val phase = (anim - i * 0.18f) / 0.5f
                    val dip = if (phase > 0f && phase < 1f) sin(phase * PI).toFloat() else 0f
                    val h = fullH * (1f - 0.78f * dip)
                    drawRoundRect(color, Offset(x, baseline - h), Size(4.6f, h), r)
                }
            }
            NavGlyph.LIBRARY -> {
                // Сам глиф рисуется статично — раскрытие задаётся поворотом rotationY
                // на слое Canvas (см. NavItem), это один плавный, не «дёрганый» жест.
                drawPath(LibraryGlyphPath1, color)
                drawPath(LibraryGlyphPath2, color)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBottomNavigationBar() {
    BookechiTheme {
        BottomNavigationBar(navController = rememberNavController())
    }
}
