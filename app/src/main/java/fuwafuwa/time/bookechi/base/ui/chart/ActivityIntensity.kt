package fuwafuwa.time.bookechi.base.ui.chart

/**
 * Уровень интенсивности чтения для цветовой градации
 */
enum class ActivityIntensity {
    NONE,      // Не читал
    VERY_LOW,  // 1-10 страниц
    LOW,       // 10-30 страниц
    MEDIUM,    // 11-30 страниц
    HIGH,      // 31-60 страниц
    VERY_HIGH  // 60+ страниц
}

/**
 * Определяет интенсивность относительно максимального значения (как на GitHub).
 * Разбивает на квартили: 0%, 1-25%, 26-50%, 51-75%, 76-100% от максимума.
 */
fun getRelativeActivityIntensity(pagesRead: Int, maxPages: Int): ActivityIntensity {
    if (pagesRead <= 0) return ActivityIntensity.NONE
    if (maxPages <= 0) return ActivityIntensity.NONE

    val percentage = (pagesRead.toFloat() / maxPages.toFloat() * 100).toInt()

    return when {
        percentage <= 10 -> ActivityIntensity.VERY_LOW
        percentage <= 25 -> ActivityIntensity.LOW
        percentage <= 50 -> ActivityIntensity.MEDIUM
        percentage <= 75 -> ActivityIntensity.HIGH
        else -> ActivityIntensity.VERY_HIGH
    }
}
