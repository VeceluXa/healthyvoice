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
import com.danilovfa.libs.recorder.config.AudioRecordConfig
import com.danilovfa.libs.recorder.recorder.wav.WavHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File
import java.io.FileInputStream
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

        val recordingId = dao.addRecording(recording.toEntity())

        return@withContext recording.copy(
            id = recordingId
        )
    }

    override suspend fun endRecording(id: Long): Result<Recording> =
        try {
            val recording = dao.getRecording(id)?.toDomain(context)?.let { recording ->
                val file = recording.file

                val audioData = getAudioDataFromFile(file)

                val updatedRecording = recording.copy(
                    durationMillis = audioData.audioDurationMillis,
                    cutStart = 0,
                    cutEnd = audioData.audioDurationMillis
                )

                dao.updateRecording(updatedRecording.toEntity())
                updatedRecording
            } ?: throw Exception("Recording not found")

            Result.success(recording)
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
                Result.success(recording)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private suspend fun loadRecordingShort(file: File): Result<Array<Short>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val shortArray = ShortArray((file.length() / 2).toInt())

                FileInputStream(file).use { inputStream ->

                    inputStream.skip(WavHeader.HEADER_SIZE_BYTES.toLong())

                    var byteHigh = inputStream.read()
                    var byteLow = inputStream.read()
                    var i = 0

                    while (byteHigh != -1 && byteLow != -1) {
                        shortArray[i] =
                            (((byteHigh and 0xFF) shl 8) or (byteLow and 0xFF)).toShort()

                        i++
                        byteLow = inputStream.read()
                        byteHigh = inputStream.read()
                    }
                }

                Result.success(shortArray.toTypedArray())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getFullRecording(recordingId: Long): Result<RecordingFull> =
        withContext(ioDispatcher) {
            return@withContext try {
                val recordingFull =
                    dao.getRecording(recordingId)?.toDomain(context)?.let { recording ->
                        val rawData = loadRecordingShort(recording.file).getOrElse {
                            return@withContext Result.failure(Exception("Couldn't load recording"))
                        }

                        val audioData = getAudioDataFromFile(recording.file).copy(
                            audioCut = AudioCut(
                                startMillis = recording.cutStart,
                                endMillis = recording.cutEnd
                            )
                        )

                        RecordingFull(
                            recording = recording,
                            rawData = rawData,
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
    ): AudioData {
        val header = ByteArray(WavHeader.HEADER_SIZE_BYTES)

        withContext(Dispatchers.IO) {
            FileInputStream(file).use {
                it.read(header)
            }
        }

        return getAudioDataFromHeader(
            header = header,
            filename = file.name
        )
    }

    private fun getAudioDataFromHeader(
        header: ByteArray,
        filename: String,
    ): AudioData {
        val config = WavHeader.getConfigFromHeader(header)
        val audioLength = WavHeader.getAudioLengthBytes(header)

        val bufferSize = AudioRecord.getMinBufferSize(
            config.frequency,
            config.channel,
            config.audioEncoding
        )

        val channels = if (config.channel == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        val bitsPerSample = when (config.audioEncoding) {
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_8BIT -> 8
            else -> 16
        }.toByte()

        val byteRate = (bitsPerSample.toLong() / 8) * config.frequency * channels.toLong()
        val durationMillis = ((audioLength.toFloat() / byteRate) * 1000).toInt()

        return getAudioData(
            filename = filename,
            config = config,
            bufferSize = bufferSize,
            durationMillis = durationMillis
        )
    }

    private fun getAudioData(
        filename: String,
        config: AudioRecordConfig,
        bufferSize: Int,
        durationMillis: Int
    ): AudioData = AudioData(
        filename = filename,
        frequency = config.frequency,
        channels = when (config.channel) {
            AudioFormat.CHANNEL_IN_MONO -> 1
            else -> 2
        },
        bitsPerSample = when (config.audioEncoding) {
            AudioFormat.ENCODING_PCM_16BIT -> 16
            else -> 8
        },
        bufferSize = bufferSize,
        audioDurationMillis = durationMillis
    )

    companion object {

        private const val WAV_FORMAT = ".wav"
    }
}