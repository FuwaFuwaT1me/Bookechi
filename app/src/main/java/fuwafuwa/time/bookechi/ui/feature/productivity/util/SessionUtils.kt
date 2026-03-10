package fuwafuwa.time.bookechi.ui.feature.productivity.util

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import java.time.LocalDate

fun List<DailyReadingStats>.getCurrentStreak(): Int {
    if (isEmpty()) return 0

    val sortedDates = map { it.localDate }.distinct().sortedDescending()
    val today = LocalDate.now()

    var streak = 0
    var expectedDate = today

    for (date in sortedDates) {
        if (date == expectedDate || date == expectedDate.minusDays(1)) {
            streak++
            expectedDate = date.minusDays(1)
        } else if (date < expectedDate.minusDays(1)) {
            break
        }
    }

    return streak
}
