package com.danilovfa.domain.common.model

import kotlinx.datetime.Instant
import java.io.File

data class Recording(
    val id: Long = 0L,
    val patientId: Long,
    val filename: String,
    val timestamp: Instant,
    val durationMillis: Int,
    val cutStart: Int,
    val cutEnd: Int,
    val file: File
)
