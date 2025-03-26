package com.danilovfa.common.core.domain.time

import java.time.format.DateTimeFormatter
import java.util.Locale

object JavaDateTimeFormatters {

    private val LOCALE get() = Locale.getDefault()

    /** if locale ru 02.05.2020, 15:30:21 -> 02.05.2020 */
    val DAY_MONTH_YEAR_DOTS: DateTimeFormatter get() = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    /** if locale ru 02.05.2020, 15:30:21 -> 2 мая */
    val DAY_MONTH: DateTimeFormatter get() = DateTimeFormatter.ofPattern("d MMMM", LOCALE)

    /** if locale ru 02.05.2020, 15:30:21 -> 2 мая 2020 */
    val DAY_MONTH_YEAR: DateTimeFormatter get() = DateTimeFormatter.ofPattern("d MMMM yyyy", LOCALE)

    /** if locale ru 02.05.2020, 15:30:21 -> 2 мая 2020, 15:30  */
    val DAY_MONTH_YEAR_HOUR_MINUTE: DateTimeFormatter get() = DateTimeFormatter.ofPattern("d MMMM, HH:mm", LOCALE)

    /** if locale ru 02.05.2020, 15:30:21 -> 15:30, 02.05.2020 */
    val HOUR_MINUTE_DAY_MONTH_YEAR: DateTimeFormatter get() = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy", LOCALE)

    /** if locale ru 02.05.2020, 15:30:21 -> 05.2020 */
    val MONTH_YEAR_DOTS: DateTimeFormatter get() = DateTimeFormatter.ofPattern("MM.yyyy")

    /** if locale ru 02.05.2020, 15:30:21 -> 02.05, 15:30 */
    val DAY_MONTH_TIME: DateTimeFormatter get() = DateTimeFormatter.ofPattern("dd.MM, HH:mm")

    /** if locale ru 02.05.2020, 15:30:21 -> 2 мая, 15:30 */
    val DAY_FULL_MONTH_TIME: DateTimeFormatter get() = DateTimeFormatter.ofPattern("d MMMM, HH:mm")

    /** if locale ru 02.05.2020, 15:30:21 -> 2020-05-02 */
    val DAY_YEAR_MONTH_DAY: DateTimeFormatter get() = DateTimeFormatter.ofPattern("yyyy-MMMM-dd")

    /** 02.05.2020, 15:30:21 -> 2020.05.02T15:30:21.SSSZ */
    val ISO_8601_24H_FULL_FORMAT: DateTimeFormatter get() = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
}