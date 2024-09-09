package com.danilovfa.libs.recorder.recorder

import java.io.FileDescriptor

interface Recorder {
    fun setOutputFile(path: String)
    fun setOutputFile(fileDescriptor: FileDescriptor)
    fun prepare()
    suspend fun start()
    fun stop()
    fun resume()
    fun pause()
    fun release()
    fun getMaxAmplitude(): Int
}