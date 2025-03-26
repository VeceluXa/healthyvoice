package com.danilovfa.domain.record.repository.model

import java.util.UUID

data class Recording(
    val id: UUID,
    val audioFormat: AudioFormat,
    val filename: String
)
