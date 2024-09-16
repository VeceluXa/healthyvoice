package com.danilovfa.libs.recorder.recorder

import com.danilovfa.libs.recorder.recorder.wav.WavHeader
import com.danilovfa.libs.recorder.writer.RecordWriter
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * [AudioRecorder] for record audio and save in wav file
 */
open class WavAudioRecorder(file: File, recordWriter: RecordWriter) :
    DefaultAudioRecorder(file, recordWriter) {
    override fun stopRecording() {
        super.stopRecording()
        writeWavHeader()
    }

    @Throws(IOException::class)
    private fun writeWavHeader() {
        val wavFile = randomAccessFile(file)
        wavFile?.let {
            it.seek(0)
            it.write(
                WavHeader.getWavFileHeaderByteArray(
                    audioSource = recordWriter.getAudioSource(),
                    length = file.length()
                )
            )
            it.close()
        }
    }

    private fun randomAccessFile(file: File): RandomAccessFile? =
        runCatching { RandomAccessFile(file, "rw") }.getOrNull()
}