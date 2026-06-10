package fuwafuwa.time.bookechi.ui.theme

import androidx.compose.ui.graphics.Color

/* ============================================================================
 * Bookechi — палитра «Terracotta & Linen»
 * Тёплый минимализм: льняная бумага, ореховый текст, приглушённая терракота,
 * шалфей как второй акцент. Подробности — docs/design-prompt.md.
 * ============================================================================ */

// --- Светлая тема: сырые токены ----------------------------------------------
val LinenCanvas = Color(0xFFF4ECE1)
val LinenSurface = Color(0xFFFBF6EF)
val LinenSurfaceWhite = Color(0xFFFFFFFF)
val LinenLibraryBg = Color(0xFFFFF9F6)
val WarmStroke = Color(0xFFE4D9CC)
val WarmDivider = Color(0xFFD9CCBC)

val Espresso = Color(0xFF382A20)   // textPrimary
val Taupe = Color(0xFF8C7C6E)      // textSecondary

val Terracotta = Color(0xFFBE5E3B)        // accent
val TerracottaDeep = Color(0xFF9E4A2C)    // accent deep
val TerracottaPressed = Color(0xFF843B22)
val TerracottaSoft = Color(0xFFE8C9B6)    // tint background

val Sage = Color(0xFF7C8A6E)              // secondary accent
val SageSoft = Color(0xFFDDE3D2)          // insight plinth bg

val CoverPlaceholder = Color(0xFFA08B7C)
val ChipBg = Color(0xFFEBE2D6)
val CardTint = Color(0xFFEFE0D2)
val AddBookBg = Color(0xFFE0CFC0)

// Стрик
val StreakBadge = Color(0xFFF0C9A8)
val StreakGradientStart = Color(0xFFFBE9D6)
val StreakGradientEnd = Color(0xFFF3D8BF)
val StreakCurrentDay = Color(0xFFC9A98C)
val Fire = Color(0xFFBE5E3B)

// Heatmap 0→5 (светлая)
val Heat0 = Color(0xFFF2E7DC)
val Heat0Stroke = Color(0xFFE0D3C4)
val Heat1 = Color(0xFFF0D2BC)
val Heat2 = Color(0xFFE0A786)
val Heat3 = Color(0xFFC97A53)
val Heat4 = Color(0xFFA85636)
val Heat5 = Color(0xFF7E3A22)

// --- Тёмная тема: сырые токены (тёплая ореховая, НЕ серый Material) -----------
val DarkCanvas = Color(0xFF1C1611)
val DarkSurface = Color(0xFF261D17)
val DarkSurfaceElevated = Color(0xFF31261E)
val DarkStroke = Color(0xFF3C2F26)

val DarkTextPrimary = Color(0xFFF0E7DC)
val DarkTextSecondary = Color(0xFFAE9D8E)

val DarkAccent = Color(0xFFCE6E48)
val DarkAccentHover = Color(0xFFE08960)
val DarkAccentDeep = Color(0xFFB4583A)
val DarkSage = Color(0xFF8FA07E)
val DarkSageSoft = Color(0xFF2E3A2A)

val DarkChipBg = Color(0xFF31261E)
val DarkCardTint = Color(0xFF31261E)

val DarkStreakGradientStart = Color(0xFF3A2A1E)
val DarkStreakGradientEnd = Color(0xFF2E2018)
val DarkStreakCurrentDay = Color(0xFF6E513B)

// Heatmap 0→5 (тёмная)
val DarkHeat0 = Color(0xFF2A211A)
val DarkHeat1 = Color(0xFF4A3526)
val DarkHeat2 = Color(0xFF6E472F)
val DarkHeat3 = Color(0xFF97583B)
val DarkHeat4 = Color(0xFFB86A45)
val DarkHeat5 = Color(0xFFD17E52)

/* ============================================================================
 * LEGACY-алиасы `Figma*`.
 * Значения ретюнингованы под «Terracotta & Linen», чтобы экраны, ссылающиеся
 * на эти имена, сразу получили новую палитру без правок. При переработке
 * экранов мигрируем на BookechiColors (Theme.kt) с поддержкой тёмной темы.
 * ============================================================================ */
val FigmaBackground = LinenCanvas
val FigmaBackgroundStroke = WarmStroke
val FigmaGrey = Espresso
val FigmaLightGrey = WarmDivider
val FigmaTitle = Espresso
val FigmaSubtitle = Taupe
val FigmaBookCover = CoverPlaceholder
val FigmaRedTitle = TerracottaDeep
val FigmaStreakBackground = StreakBadge
val FigmaStreakBackgroundStart = StreakGradientStart
val FigmaStreakBackgroundEnd = StreakGradientEnd
val FigmaStreakCurrentDayBackground = StreakCurrentDay
val FigmaFire = Fire
val FigmaBottomNavSelectedTab = TerracottaDeep
val FigmaProductivityHeaderItemBackground = CardTint
val FigmaPeriodSwitcherBackground = ChipBg
val FigmaActivityCellZeroActivityStroke = Heat0Stroke
val FigmaActivityCellZeroActivity = Heat0
val FigmaActivityCellOneActivity = Heat1
val FigmaActivityCellTwoActivity = Heat2
val FigmaActivityCellThreeActivity = Heat3
val FigmaActivityCellFourActivity = Heat4
val FigmaActivityCellFiveActivity = Heat5
val FigmaAddBookBackground = AddBookBg
val FigmaLibraryBackground = LinenLibraryBg

val BottomBarDivider = WarmDivider

// Прочие legacy-имена (используются в старых экранах) — ретюн на терракоту
val SuperLightGray = Color(0xFFF1F1F1)
val BlueMain = Terracotta
val BlueMainDark = TerracottaDeep
val BlackLight = Espresso
val BlackLight2 = Color(0xFF121212)
