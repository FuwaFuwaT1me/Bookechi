@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package fuwafuwa.time.bookechi.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import fuwafuwa.time.bookechi.R

/**
 * Lora — serif для заголовков/вордмарки (дизайн «Terracotta & Linen»). Вариативный шрифт,
 * нужные веса задаём через [FontVariation] (требует API 26 — наш minSdk).
 *
 * Используется точечно (онбординг, serif-заголовки), глобальная [Typography] остаётся sans.
 */
val LoraFontFamily = FontFamily(
    Font(
        R.font.lora_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        R.font.lora_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
    Font(
        R.font.lora_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700)),
    ),
)
