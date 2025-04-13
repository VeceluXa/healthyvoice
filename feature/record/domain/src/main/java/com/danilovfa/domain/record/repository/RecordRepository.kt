package com.danilovfa.domain.record.repository

import com.danilovfa.domain.common.model.Recording
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.domain.record.repository.model.RecordingFull

interface RecordRepository {
    suspend fun start(patientId: Long): Recording
    suspend fun endRecording(id: Long): Result<Recording>

    suspend fun importRecording(patientId: Long, data: ByteArray): Result<Recording>
    suspend fun saveCut(recordingId: Long, startMillis: Int, endMillis: Int)

    suspend fun getFullRecording(recordingId: Long): Result<RecordingFull>
}