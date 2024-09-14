package com.danilovfa.data.record.domain.repository

import java.io.File

interface RecordRepository {

    fun getRecordingsDir(): File

    suspend fun saveEncodedRecording(data: ByteArray): Result<String>

    suspend fun loadRecording(filename: String): Result<ByteArray>
}