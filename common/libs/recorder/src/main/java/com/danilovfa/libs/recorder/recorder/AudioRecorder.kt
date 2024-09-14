package com.danilovfa.libs.recorder.recorder

interface AudioRecorder {
    suspend fun startRecording()

    fun resumeRecording()

    fun pauseRecording()

    fun stopRecording()
}