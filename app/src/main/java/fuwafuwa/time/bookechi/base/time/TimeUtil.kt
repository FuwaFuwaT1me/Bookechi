package fuwafuwa.time.bookechi.base.time

import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

data class Date(
    val localDate: LocalDate,
    val firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
) {
    val year: Int get() = localDate.year
    val month: Int get() = localDate.monthValue
    val dayOfWeek: DayOfWeek get() = localDate.dayOfWeek
    val dayOfMonth: Int get() = localDate.dayOfMonth
    val dayOfYear: Int get() = localDate.dayOfYear
    val weekOfYear: Int get() = getWeekOfYear(localDate, firstDayOfWeek)
    val weekOfMonth: Int get() = localDate.get(WeekFields.of(firstDayOfWeek, 1).weekOfMonth())
    val dateKey: String get() = localDate.toString() // "YYYY-MM-DD"
    val isFirstDayOfMonth: Boolean get() = localDate.dayOfMonth == 1
    val isLastDayOfMonth: Boolean get() = localDate.dayOfMonth == localDate.lengthOfMonth()

    companion object {
        fun from(date: LocalDate, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Date {
            return Date(localDate = date, firstDayOfWeek = firstDayOfWeek)
        }

        fun of(year: Int, month: Int, dayOfMonth: Int, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Date {
            return Date(localDate = LocalDate.of(year, month, dayOfMonth), firstDayOfWeek = firstDayOfWeek)
        }

        fun today(clock: Clock = Clock.systemDefaultZone(), firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Date {
            return Date(localDate = LocalDate.now(clock), firstDayOfWeek = firstDayOfWeek)
        }
    }
}

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

fun getDaysInMonth(year: Int, month: Int, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): List<Date> {
    val days = mutableListOf<Date>()
    val yearMonth = YearMonth.of(year, month)
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()

    for (i in 0 until lastDay.dayOfMonth) {
        val day = firstDay.plusDays(i.toLong())
        days.add(
            Date.from(date = day, firstDayOfWeek = firstDayOfWeek)
        )
    }
    return days
}


fun getDaysInYear(year: Int, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): List<Date> {
    val days = mutableListOf<Date>()
    (1..12).forEach { month ->
        val yearMonth = YearMonth.of(year, month)
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        for (i in 0 until lastDay.dayOfMonth) {
            val day = firstDay.plusDays(i.toLong())
            days.add(
                Date.from(date = day, firstDayOfWeek = firstDayOfWeek)
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
