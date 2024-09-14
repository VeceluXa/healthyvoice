package com.danilovfa.libs.recorder.writer

import android.media.AudioRecord
import com.danilovfa.core.library.log.LOG_TAG
import com.danilovfa.libs.recorder.chunk.AudioChunk
import com.danilovfa.libs.recorder.chunk.ByteArrayAudioChunk
import com.danilovfa.libs.recorder.chunk.checkChunkAvailable
import com.danilovfa.libs.recorder.source.AudioSource
import com.danilovfa.libs.recorder.source.DefaultAudioSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.OutputStream

open class DefaultRecordWriter(
    private val audioSource: AudioSource = DefaultAudioSource(),
    private val amplitudeListener: ((Int) -> Unit)? = null,
    private val chunkAvailableListener: ((AudioChunk) -> Unit)? = null
) : RecordWriter {

    private var confirmStart: Boolean = false

    override suspend fun startRecording(outputStream: OutputStream) {
        audioSource.preProcessAudioRecord()
        write(
            audioRecord = getAudioSource().getAudioRecord(),
            bufferSize = getAudioSource().getBufferSize(),
            outputStream = outputStream
        )
    }

    override fun stopRecording() {
        if (confirmStart.not()) {
            Timber.tag(LOG_TAG).e("Tried to stopRecording() before initialization!")
            return
        }

        audioSource.getAudioRecord().stop()
        audioSource.getAudioRecord().release()
        confirmStart = false
    }

    override fun getAudioSource(): AudioSource = audioSource

    private suspend fun write(
        audioRecord: AudioRecord,
        bufferSize: Int,
        outputStream: OutputStream
    ) {
        val audioChunk = ByteArrayAudioChunk(ByteArray(bufferSize))

        while (audioSource.isRecordAvailable()) {
            if (!confirmStart) confirmStart = true

            audioChunk.setReadCount(audioRecord.read(audioChunk.bytes, 0, bufferSize))
            if (!audioChunk.checkChunkAvailable()) continue

            amplitudeListener?.invoke(audioChunk.getMaxAmplitude(bufferSize))
            chunkAvailableListener?.invoke(audioChunk)

            withContext(Dispatchers.IO) {
                outputStream.write(audioChunk.bytes)
            }
        }
    }
}