package com.danilovfa.domain.common.model

import kotlinx.datetime.LocalDateTime

data class Analysis(
    val id: Long = 0L,
    val patientId: String,
    val filename: String,
    val timestamp: LocalDateTime,
    val cutStart: Int,
    val cutEnd: Int,
    val j0: Float,
    val j3: Float,
    val j5: Float,
    val s3: Float,
    val s5: Float,
    val s11: Float,
    val f0: Float,
    val f0sd: Float
)
