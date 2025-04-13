package com.danilovfa.domain.common.model

import kotlinx.datetime.Instant

data class Analysis(
    val recordingId: Long,
    val patientId: Long,
    val timestamp: Instant,
    val j1: Float,
    val j3: Float,
    val j5: Float,
    val s1: Float,
    val s3: Float,
    val s5: Float,
    val s11: Float,
    val f0: Float,
    val f0sd: Float
)
