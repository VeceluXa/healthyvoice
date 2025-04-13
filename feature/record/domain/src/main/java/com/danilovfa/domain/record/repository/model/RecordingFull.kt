package com.danilovfa.domain.record.repository.model

import com.danilovfa.domain.common.model.Recording

data class RecordingFull(
    val recording: Recording,
    val audioData: AudioData,
    val rawData: Array<Short>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordingFull

        if (recording != other.recording) return false
        if (audioData != other.audioData) return false
        if (!rawData.contentEquals(other.rawData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recording.hashCode()
        result = 31 * result + audioData.hashCode()
        result = 31 * result + rawData.contentHashCode()
        return result
    }
}