package com.danilovfa.common.core.domain.time

import com.danilovfa.common.core.domain.time.KotlinDateTimeFormatters.isoDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

/**
 * Convert UTC to local time (UTC+3)
 */
fun LocalDateTime.utcToLocal(): LocalDateTime =
    this.toInstant(TimeZone.UTC).toLocalDateTime(TimeZone.of("Europe/Moscow"))

/**
 * Convert UTC to epoch milliseconds
 */
fun LocalDateTime.utcToEpochMilliseconds(): Long =
    this.toInstant(TimeZone.UTC).toEpochMilliseconds()

/**
 * Convert epoch milliseconds to UTC datetime
 */
fun LocalDateTime.Companion.utcFromEpochMilliseconds(epochMilliseconds: Long): LocalDateTime =
    Instant.fromEpochMilliseconds(epochMilliseconds).toLocalDateTime(TimeZone.UTC)

/**
 * Get current datetime in UTC
 */
fun LocalDateTime.Companion.now(): LocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.UTC)

/**
 * Convert local time (UTC+3) to UTC
 */
fun LocalDateTime.localToUtc(): LocalDateTime =
    this.toInstant(TimeZone.of("Europe/Moscow")).toLocalDateTime(TimeZone.UTC)

/**
 * Convert LocalDateTime to ISO-8601 string
 */
fun LocalDateTime.toIsoString(): String =
    this.format(LocalDateTime.Format { isoDateTime() })

fun LocalDateTime.format(builder: DateTimeFormatBuilder.WithDateTime.() -> Unit) =
    this.format(LocalDateTime.Format {
        builder()
    })

fun LocalDate.format(builder: DateTimeFormatBuilder.WithDate.() -> Unit) =
    this.format(LocalDate.Format { builder() })

/**
 * Convert Duration to SS:mm
 */
fun Duration.toSeconds(): String {
    val seconds = (inWholeSeconds % 60)
        .toString()
        .padStart(2, '0')

    val milliseconds = (inWholeMilliseconds % 1000 / 10)
        .toString()
        .padStart(2, '0')
    return "$seconds.$milliseconds"
}