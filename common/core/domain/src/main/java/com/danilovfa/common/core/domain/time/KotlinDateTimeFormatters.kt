package com.danilovfa.common.core.domain.time

import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.char

object KotlinDateTimeFormatters {

    /**
     * 01.11.2024 -> 2024-11-01
     */
    fun DateTimeFormatBuilder.WithDate.isoDate() {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
    }

    /**
     * 05:34:21.321 -> 05:34:21
     */
    fun DateTimeFormatBuilder.WithTime.time() {
        hour()
        char(':')
        minute()
        char(':')
        second()
    }

    /**
     * 05:34:21.321 -> 05:34:21.321
     */
    fun DateTimeFormatBuilder.WithTime.isoTime() {
        hour()
        char(':')
        minute()
        char(':')
        second()
        char('.')
        secondFraction(3)
    }

    /**
     * 05:34:21.321 -> 05:34
     */
    fun DateTimeFormatBuilder.WithTime.timeWithoutSeconds() {
        hour()
        char(':')
        minute()
    }

    /**
     * 01.11.2024, 05:34:21.321 -> 2024-11-01T05:34:21Z
     */
    fun DateTimeFormatBuilder.WithDateTime.isoDateTimeWithoutMilliseconds() {
        isoDate()
        char('T')
        time()
        char('Z')
    }

    /**
     * 01.11.2024, 05:34:21.321 -> 2024-11-01T05:34:21.321Z
     */
    fun DateTimeFormatBuilder.WithDateTime.isoDateTime() {
        isoDate()
        char('T')
        isoTime()
        char('Z')
    }

    /**
     * 01.11.2024 -> 01.11.2024
     */
    fun DateTimeFormatBuilder.WithDate.date() {
        dayOfMonth()
        char('.')
        monthNumber()
        char('.')
        year()
    }

    /**
     * 01.11.2024, 05:34:21.321 -> 01.11.2024, 05:34
     */
    fun DateTimeFormatBuilder.WithDateTime.dateTime() {
        date()
        chars(", ")
        timeWithoutSeconds()
    }
}