package com.danilovfa.core.library.time

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.format(formatter: DateTimeFormatter): String = this
    .toJavaLocalDateTime()
    .format(formatter)