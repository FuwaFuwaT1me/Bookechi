package fuwafuwa.time.bookechi.utils.file

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fuwafuwa.time.bookechi.R

@Composable
fun parseWeekDayNumberToShortName(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> stringResource(R.string.date_weekday_mon_short)
        2 -> stringResource(R.string.date_weekday_tue_short)
        3 -> stringResource(R.string.date_weekday_wed_short)
        4 -> stringResource(R.string.date_weekday_thu_short)
        5 -> stringResource(R.string.date_weekday_fri_short)
        6 -> stringResource(R.string.date_weekday_sat_short)
        7 -> stringResource(R.string.date_weekday_sun_short)
        else -> ""
    }
}
