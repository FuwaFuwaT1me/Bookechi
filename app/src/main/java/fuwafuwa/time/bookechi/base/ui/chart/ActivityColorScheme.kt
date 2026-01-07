package fuwafuwa.time.bookechi.base.ui.chart

import androidx.compose.ui.graphics.Color
import fuwafuwa.time.bookechi.data.model.ActivityIntensity

sealed class ActivityColorScheme {
    abstract fun getColor(intensity: ActivityIntensity): Color
    abstract val emptyColor: Color

    data object Activity : ActivityColorScheme() {
        override val emptyColor = Color(0xFFEBEDF0)
        override fun getColor(intensity: ActivityIntensity): Color = when (intensity) {
            ActivityIntensity.NONE -> emptyColor
            ActivityIntensity.LOW -> Color(0xFF9BE9A8)
            ActivityIntensity.MEDIUM -> Color(0xFF40C463)
            ActivityIntensity.HIGH -> Color(0xFF30A14E)
            ActivityIntensity.VERY_HIGH -> Color(0xFF216E39)
        }
    }

    data object Orange : ActivityColorScheme() {
        override val emptyColor = Color(0xFFEBEDF0)
        override fun getColor(intensity: ActivityIntensity): Color = when (intensity) {
            ActivityIntensity.NONE -> emptyColor
            ActivityIntensity.LOW -> Color(0xFFFFD699)
            ActivityIntensity.MEDIUM -> Color(0xFFFFAD33)
            ActivityIntensity.HIGH -> Color(0xFFFF8C00)
            ActivityIntensity.VERY_HIGH -> Color(0xFFCC7000)
        }
    }

    data object Blue : ActivityColorScheme() {
        override val emptyColor = Color(0xFFEBEDF0)
        override fun getColor(intensity: ActivityIntensity): Color = when (intensity) {
            ActivityIntensity.NONE -> emptyColor
            ActivityIntensity.LOW -> Color(0xFFB3D9FF)
            ActivityIntensity.MEDIUM -> Color(0xFF66B3FF)
            ActivityIntensity.HIGH -> Color(0xFF1A8CFF)
            ActivityIntensity.VERY_HIGH -> Color(0xFF0066CC)
        }
    }

    data object Purple : ActivityColorScheme() {
        override val emptyColor = Color(0xFFEBEDF0)
        override fun getColor(intensity: ActivityIntensity): Color = when (intensity) {
            ActivityIntensity.NONE -> emptyColor
            ActivityIntensity.LOW -> Color(0xFFE0B3FF)
            ActivityIntensity.MEDIUM -> Color(0xFFC266FF)
            ActivityIntensity.HIGH -> Color(0xFF9933FF)
            ActivityIntensity.VERY_HIGH -> Color(0xFF7A00CC)
        }
    }
}
