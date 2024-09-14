package com.danilovfa.data.common.model

import java.util.UUID

data class Recording(
    val id: UUID,
    val audioFormat: AudioFormat,
    val filename: String
)
