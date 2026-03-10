package fuwafuwa.time.bookechi.ui.feature.productivity.ui

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import java.time.LocalDate
import java.time.YearMonth

object ProductivityPreviewData {
    fun generateMonthData(year: Int = 2026, month: Int = 1): List<DailyReadingStats> {
        return buildList {
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            (1..daysInMonth).forEach { day ->
                val pages = if (day % 3 == 0) 0 else (0..100).random()
                val date = LocalDate.of(year, month, day)

                add(
                    DailyReadingStats(
                        date = date.toString(),
                        totalPagesRead = pages,
                        totalReadingTimeMinutes = 5,
                        booksRead = 1
                    )
                )
            }
        }
    }
}
