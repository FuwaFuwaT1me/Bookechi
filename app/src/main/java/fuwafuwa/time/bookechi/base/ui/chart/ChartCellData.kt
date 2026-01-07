package fuwafuwa.time.bookechi.base.ui.chart

import fuwafuwa.time.bookechi.base.time.Date
import fuwafuwa.time.bookechi.data.model.ActivityIntensity

data class ChartCellData(
    val date: Date?,
    val intensity: ActivityIntensity = ActivityIntensity.NONE,
    val pagesRead: Int = 0
)
