package fuwafuwa.time.bookechi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/* ============================================================================
 * Семантические токены приложения, которых нет в Material ColorScheme
 * (стрик, heatmap, шалфей, тинты и т.п.). Доступ — BookechiTheme.colors.
 * При переработке экранов берём цвета отсюда, а не из legacy `Figma*`.
 * ============================================================================ */
@Immutable
data class BookechiColors(
    val canvas: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val stroke: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentDeep: Color,
    val accentSoft: Color,
    val sage: Color,
    val sageSoft: Color,
    val coverPlaceholder: Color,
    val chipBg: Color,
    val cardTint: Color,
    val streakGradientStart: Color,
    val streakGradientEnd: Color,
    val streakCurrentDay: Color,
    val fire: Color,
    val heatmap: List<Color>,
    val heatZeroStroke: Color,
    val isDark: Boolean,
)

private val LightBookechiColors = BookechiColors(
    canvas = LinenCanvas,
    surface = LinenSurface,
    surfaceElevated = LinenSurfaceWhite,
    stroke = WarmStroke,
    divider = WarmDivider,
    textPrimary = Espresso,
    textSecondary = Taupe,
    accent = Terracotta,
    accentDeep = TerracottaDeep,
    accentSoft = TerracottaSoft,
    sage = Sage,
    sageSoft = SageSoft,
    coverPlaceholder = CoverPlaceholder,
    chipBg = ChipBg,
    cardTint = CardTint,
    streakGradientStart = StreakGradientStart,
    streakGradientEnd = StreakGradientEnd,
    streakCurrentDay = StreakCurrentDay,
    fire = Fire,
    heatmap = listOf(Heat0, Heat1, Heat2, Heat3, Heat4, Heat5),
    heatZeroStroke = Heat0Stroke,
    isDark = false,
)

private val DarkBookechiColors = BookechiColors(
    canvas = DarkCanvas,
    surface = DarkSurface,
    surfaceElevated = DarkSurfaceElevated,
    stroke = DarkStroke,
    divider = DarkStroke,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    accent = DarkAccent,
    accentDeep = DarkAccentDeep,
    accentSoft = DarkAccentHover,
    sage = DarkSage,
    sageSoft = DarkSageSoft,
    coverPlaceholder = CoverPlaceholder,
    chipBg = DarkChipBg,
    cardTint = DarkCardTint,
    streakGradientStart = DarkStreakGradientStart,
    streakGradientEnd = DarkStreakGradientEnd,
    streakCurrentDay = DarkStreakCurrentDay,
    fire = DarkAccent,
    heatmap = listOf(DarkHeat0, DarkHeat1, DarkHeat2, DarkHeat3, DarkHeat4, DarkHeat5),
    heatZeroStroke = DarkStroke,
    isDark = true,
)

val LocalBookechiColors = staticCompositionLocalOf { LightBookechiColors }

/** Колбэк переключения светлой/тёмной темы, прокидывается из MainActivity. */
val LocalThemeToggle = staticCompositionLocalOf<() -> Unit> { {} }

/** Доступ к семантическим токенам: BookechiTheme.colors.* */
object BookechiTheme {
    val colors: BookechiColors
        @Composable
        get() = LocalBookechiColors.current
}

/* --- Material ColorScheme (для стандартных компонентов) ---------------------- */
private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    secondary = Sage,
    onSecondary = Color.White,
    tertiary = TerracottaDeep,
    background = LinenCanvas,
    onBackground = Espresso,
    surface = LinenSurface,
    onSurface = Espresso,
    surfaceVariant = ChipBg,
    onSurfaceVariant = Taupe,
    outline = WarmStroke,
    error = TerracottaDeep,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = Color(0xFF1C1611),
    secondary = DarkSage,
    onSecondary = Color(0xFF1C1611),
    tertiary = DarkAccentHover,
    background = DarkCanvas,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkChipBg,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkStroke,
    error = DarkAccentHover,
)

@Composable
fun BookechiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val bookechiColors = if (darkTheme) DarkBookechiColors else LightBookechiColors

    CompositionLocalProvider(LocalBookechiColors provides bookechiColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
