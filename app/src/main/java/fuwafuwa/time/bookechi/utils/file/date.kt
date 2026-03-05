package fuwafuwa.time.bookechi.utils.file

fun parseWeekDayNumberToShortName(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "Пн"
        2 -> "Вт"
        3 -> "Ср"
        4 -> "Чт"
        5 -> "Пт"
        6 -> "Сб"
        7 -> "Вс"
        else -> ""
    }
}
