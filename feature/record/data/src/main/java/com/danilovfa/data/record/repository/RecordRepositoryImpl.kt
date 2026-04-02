package com.danilovfa.data.record.repository

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import com.danilovfa.data.common.local.database.dao.RecordingDao
import com.danilovfa.data.common.local.database.model.toDomain
import com.danilovfa.data.common.local.database.model.toEntity
import com.danilovfa.data.common.utils.RecordingUtils
import com.danilovfa.domain.common.model.Recording
import com.danilovfa.domain.record.repository.RecordRepository
import com.danilovfa.domain.record.repository.model.AudioCut
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.domain.record.repository.model.RecordingFull
import com.danilovfa.libs.recorder.recorder.wav.DecodedWavPcm
import com.danilovfa.libs.recorder.recorder.wav.WavPcmReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.coroutines.CoroutineContext

internal class RecordRepositoryImpl(
    private val context: Context,
    private val dao: RecordingDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) : RecordRepository {
    override suspend fun start(patientId: Long): Recording = withContext(ioDispatcher) {
        val recordingsDir = RecordingUtils.getRecordingsDir(context)

        val filename = "${UUID.randomUUID()}$WAV_FORMAT"

        val recording = Recording(
            patientId = patientId,
            filename = filename,
            timestamp = Clock.System.now(),
            durationMillis = 0,
            cutStart = 0,
            cutEnd = 0,
            file = File(recordingsDir, filename)
        )

        recording.file.createNewFile()

        return@withContext recording
    }

    override suspend fun endRecording(recording: Recording): Result<Recording> =
        try {
            val file = recording.file
            val audioData = getAudioDataFromFile(file)

            val updatedRecording = recording.copy(
                durationMillis = audioData.audioDurationMillis,
                cutStart = 0,
                cutEnd = audioData.audioDurationMillis
            )

            val recordingId = dao.addRecording(updatedRecording.toEntity())

            Result.success(recording.copy(id = recordingId))
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun importRecording(patientId: Long, data: ByteArray): Result<Recording> =
        withContext(ioDispatcher) {
            return@withContext try {
                val filename = "${UUID.randomUUID()}$WAV_FORMAT"
                val file = File(RecordingUtils.getRecordingsDir(context), filename)

                FileOutputStream(file).use {
                    it.write(data)
                }

                val audioData = getAudioDataFromFile(file)

                val recording = Recording(
                    patientId = patientId,
                    filename = file.name,
                    timestamp = Clock.System.now(),
                    durationMillis = audioData.audioDurationMillis,
                    cutStart = 0,
                    cutEnd = audioData.audioDurationMillis,
                    file = file
                )
                val recordingId = dao.addRecording(recording.toEntity())
                Result.success(recording.copy(id = recordingId))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private suspend fun loadRecordingShort(file: File): Result<ShortArray> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.success(WavPcmReader.read(file).samples)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getFullRecording(recordingId: Long): Result<RecordingFull> =
        withContext(ioDispatcher) {
            return@withContext try {
                val recordingFull =
                    dao.getRecording(recordingId)?.toDomain(context)?.let { recording ->
                        val decoded = runCatching { WavPcmReader.read(recording.file) }.getOrElse {
                            return@withContext Result.failure(Exception("Couldn't load recording", it))
                        }

                        val audioData = getAudioDataFromDecoded(
                            filename = recording.file.name,
                            decoded = decoded
                        ).copy(
                            audioCut = AudioCut(
                                startMillis = recording.cutStart,
                                endMillis = recording.cutEnd
                            )
                        )

                        RecordingFull(
                            recording = recording,
                            rawData = decoded.samples,
                            audioData = audioData
                        )
                    } ?: throw Exception("Couldn't load recording")

                Result.success(recordingFull)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun saveCut(
        recordingId: Long,
        startMillis: Int,
        endMillis: Int
    ) {
        dao.getRecording(recordingId)?.let { recording ->
            val newRecording = recording.copy(
                cutStartMillis = startMillis,
                cutEndMillis = endMillis
            )

            dao.updateRecording(newRecording)
        }
    }

    private suspend fun getAudioDataFromFile(
        file: File
    ): AudioData = withContext(Dispatchers.IO) {
        getAudioDataFromDecoded(
            filename = file.name,
            decoded = WavPcmReader.read(file)
        )
    }

    private fun getAudioDataFromDecoded(
        filename: String,
        decoded: DecodedWavPcm
    ): AudioData = AudioData(
        filename = filename,
        frequency = decoded.config.frequency,
        channels = decoded.channels,
        bitsPerSample = decoded.bitsPerSample,
        bufferSize = AudioRecord.getMinBufferSize(
            decoded.config.frequency,
            decoded.config.channel,
            decoded.config.audioEncoding
        ),
        audioDurationMillis = decoded.durationMillis
    )

    companion object {

        private const val WAV_FORMAT = ".wav"
    }
}
