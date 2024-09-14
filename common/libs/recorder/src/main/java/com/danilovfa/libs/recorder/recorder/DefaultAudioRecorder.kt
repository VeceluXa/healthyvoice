package com.danilovfa.libs.recorder.recorder

import com.danilovfa.libs.recorder.writer.DefaultRecordWriter
import com.danilovfa.libs.recorder.writer.RecordWriter
import java.io.File
import java.io.FileOutputStream

open class DefaultAudioRecorder(
    protected val file: File,
    protected val recordWriter: RecordWriter = DefaultRecordWriter()
) : AudioRecorder {

    private val outputStream by lazy { FileOutputStream(file) }

    override suspend fun startRecording() {
        recordWriter.startRecording(outputStream)
    }

    override fun resumeRecording() {
        recordWriter.getAudioSource().setRecordAvailable(true)
    }

    override fun pauseRecording() {
        recordWriter.getAudioSource().setRecordAvailable(false)
    }

    override fun stopRecording() {
        recordWriter.stopRecording()
        recordWriter.getAudioSource().setRecordAvailable(false)
        outputStream.flush()
        outputStream.close()
    }
}