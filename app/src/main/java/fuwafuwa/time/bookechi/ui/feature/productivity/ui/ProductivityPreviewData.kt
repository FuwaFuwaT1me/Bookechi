package fuwafuwa.time.bookechi.ui.feature.productivity.ui

import fuwafuwa.time.bookechi.data.model.DailyReadingStats
import java.time.LocalDate
import java.time.YearMonth

object ProductivityPreviewData {

    fun generateMonthData(year: Int = 2026, month: Int = 1): List<DailyReadingStats> {
        return buildList {
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            (1..daysInMonth).forEach { day ->
                val pages = if (day % 8 == 0) 0 else (0..100).random()
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

    fun generateYearData(year: Int = 2026): List<DailyReadingStats> {
        // Явный разброс месячных сумм (пик — 1207, как в макете), чтобы в превью
        // была видна зависимость насыщенности столбика от активности месяца.
        val monthlyTotals = listOf(120, 340, 80, 520, 260, 900, 1207, 430, 180, 60, 700, 300)
        return buildList {
            monthlyTotals.forEachIndexed { index, total ->
                val month = index + 1
                // Для годового графика важна только сумма за месяц — кладём её одним днём.
                add(
                    DailyReadingStats(
                        date = LocalDate.of(year, month, 15).toString(),
                        totalPagesRead = total,
                        totalReadingTimeMinutes = 0,
                        booksRead = 1
                    )
                )
            }
        }
    }
}
