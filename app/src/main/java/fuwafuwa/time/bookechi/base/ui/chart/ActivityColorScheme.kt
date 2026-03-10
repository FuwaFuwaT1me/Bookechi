package fuwafuwa.time.bookechi.base.ui.chart

import androidx.compose.ui.graphics.Color
import fuwafuwa.time.bookechi.data.model.ActivityIntensity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellFiveActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellFourActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellOneActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellThreeActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellTwoActivity
import fuwafuwa.time.bookechi.ui.theme.FigmaActivityCellZeroActivity

sealed class ActivityColorScheme {
    abstract fun getColor(intensity: ActivityIntensity): Color
    abstract val emptyColor: Color

    data object OrangeActivity : ActivityColorScheme() {
        override val emptyColor = Color(0xFFEBEDF0)
        override fun getColor(intensity: ActivityIntensity): Color = when (intensity) {
            ActivityIntensity.NONE -> FigmaActivityCellZeroActivity
            ActivityIntensity.VERY_LOW -> FigmaActivityCellOneActivity
            ActivityIntensity.LOW -> FigmaActivityCellTwoActivity
            ActivityIntensity.MEDIUM -> FigmaActivityCellThreeActivity
            ActivityIntensity.HIGH -> FigmaActivityCellFourActivity
            ActivityIntensity.VERY_HIGH -> FigmaActivityCellFiveActivity
        }
    }
}
