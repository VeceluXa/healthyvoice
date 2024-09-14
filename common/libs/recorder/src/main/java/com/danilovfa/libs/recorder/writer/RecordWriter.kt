package com.danilovfa.libs.recorder.writer

import com.danilovfa.libs.recorder.source.AudioSource
import java.io.OutputStream

interface RecordWriter {

    /**
     * Start recording feature using [getAudioSource]
     *
     * Basically, it read from [android.media.AudioRecord] and write to [OutputStream]
     */
    suspend fun startRecording(outputStream: OutputStream)

    /**
     * Stop recording feature
     */
    fun stopRecording()

    /**
     * get [AudioSource] which is used for reading from [android.media.AudioRecord]
     */
    fun getAudioSource(): AudioSource
}