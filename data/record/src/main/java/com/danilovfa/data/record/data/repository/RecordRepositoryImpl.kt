package com.danilovfa.data.record.data.repository

import android.content.Context
import com.danilovfa.data.record.domain.repository.RecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import kotlin.coroutines.CoroutineContext

internal class RecordRepositoryImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) : RecordRepository {

    override suspend fun saveEncodedRecording(data: ByteArray): Result<String> = withContext(ioDispatcher) {
        val filename = "${UUID.randomUUID()}$WAV_FORMAT"

        val file = File(getRecordingsDir(), filename)

        return@withContext try {
            FileOutputStream(file).use {
                it.write(data)
            }

            Result.success(filename)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadRecording(filename: String): Result<ByteArray> = withContext(ioDispatcher) {
        val file = File(getRecordingsDir(), filename)

        return@withContext try {
            FileInputStream(file).use {
                Result.success(it.readBytes())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getRecordingsDir(): File {
        val cacheDir = context.cacheDir
        val recordingsDir = File(cacheDir, RECORDINGS_DIR)

        if (recordingsDir.exists().not()) {
            recordingsDir.mkdirs()
        }

        return recordingsDir
    }

    companion object {
        private const val RECORDINGS_DIR = "recordings"
        private const val WAV_FORMAT = ".wav"
    }
}