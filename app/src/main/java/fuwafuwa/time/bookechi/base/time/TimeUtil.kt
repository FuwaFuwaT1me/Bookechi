package fuwafuwa.time.bookechi.base.time

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

data class Date(
    val year: Int,
    val month: Int,
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int,
    val dayOfYear: Int,
    val weekOfYear: Int,
    val weekOfMonth: Int,
    val dateKey: String, // "YYYY-MM-DD"
    val isFirstDayOfMonth: Boolean = false,
    val isLastDayOfMonth: Boolean = false,
)

fun getWeeksInYear(year: Int): Int {
    val firstDay = YearMonth.of(year, 1).atDay(1)
    val lastDay = YearMonth.of(year, 12).atEndOfMonth()

    val firstWeek = getWeekOfYear(firstDay, DayOfWeek.MONDAY)
    val lastWeek = getWeekOfYear(lastDay, DayOfWeek.MONDAY)

    return lastWeek - firstWeek + 1
}

fun getWeeksInMonth(year: Int, month: Int, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Int {
    val yearMonth = YearMonth.of(year, month)
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()
    
    val firstWeek = getWeekOfYear(firstDay, firstDayOfWeek)
    val lastWeek = getWeekOfYear(lastDay, firstDayOfWeek)
    
    return lastWeek - firstWeek + 1
}

fun getDaysInMonth(year: Int, month: Int): List<Date> {
    val days = mutableListOf<Date>()
    val yearMonth = YearMonth.of(year, month)
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()

    for (i in 0 until lastDay.dayOfMonth) {
        val day = firstDay.plusDays(i.toLong())
        val week = day.get(
            WeekFields.of(DayOfWeek.MONDAY, 1).weekOfMonth()
        )

        days.add(
            Date(
                year = year,
                month = month,
                dayOfWeek = day.dayOfWeek,
                dayOfMonth = day.dayOfMonth,
                dayOfYear = day.dayOfYear,
                weekOfYear = getWeekOfYear(day, DayOfWeek.MONDAY),
                weekOfMonth = week,
                dateKey = day.toString(), // ISO format: YYYY-MM-DD
                isFirstDayOfMonth = i == 0,
                isLastDayOfMonth = day == lastDay,
            )
        )
    }
    return days
}


fun getDaysInYear(year: Int): List<Date> {
    val days = mutableListOf<Date>()
    (1..12).forEach { month ->
        val yearMonth = YearMonth.of(year, month)
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        for (i in 0 until lastDay.dayOfMonth) {
            val day = firstDay.plusDays(i.toLong())
            val week = day.get(
                WeekFields.of(DayOfWeek.MONDAY, 1).weekOfMonth()
            )

            days.add(
                Date(
                    year = year,
                    month = month,
                    dayOfWeek = day.dayOfWeek,
                    dayOfMonth = day.dayOfMonth,
                    dayOfYear = day.dayOfYear,
                    weekOfYear = getWeekOfYear(day, DayOfWeek.MONDAY),
                    weekOfMonth = week,
                    dateKey = day.toString(), // ISO format: YYYY-MM-DD
                    isFirstDayOfMonth = i == 0,
                    isLastDayOfMonth = day == lastDay,
                )
            )
        }
    }
    return days
}

fun getWeekOfYear(date: LocalDate, firstDayOfWeek: DayOfWeek): Int {
    var week = date.get(WeekFields.of(firstDayOfWeek, 1).weekOfYear())
    
    // Если месяц январь и неделя >= 52, это может быть неделя предыдущего года
    if (date.monthValue == 1 && week >= 52) {
        week = 0
    }
    return week
}
