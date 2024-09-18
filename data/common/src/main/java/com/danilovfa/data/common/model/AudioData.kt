package com.danilovfa.data.common.model

import kotlinx.serialization.Serializable

@Serializable
data class AudioData(
    val filename: String,
    val frequency: Int,
    val channel: Int,
    val bitsPerSample: Int,
    val bufferSize: Int,
    val audioDurationMillis: Int,
    val audioCut: AudioCut? = null
)

@Serializable
data class AudioCut(
    val startMillis: Int,
    val endMillis: Int
)
