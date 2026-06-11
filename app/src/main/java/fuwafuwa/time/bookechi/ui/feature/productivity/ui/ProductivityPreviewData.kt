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
        // Форма как в макете year chart: рост к пику в Мае (1207, «Лучший месяц»),
        // текущий месяц Июн — небольшой, Июл..Дек ещё впереди (0). Насыщенность
        // столбика зависит от активности месяца.
        val monthlyTotals = listOf(905, 360, 580, 700, 1207, 220, 0, 0, 0, 0, 0, 0)
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
