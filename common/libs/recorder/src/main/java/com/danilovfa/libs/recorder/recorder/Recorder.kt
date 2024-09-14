package com.danilovfa.libs.recorder.recorder

import kotlinx.coroutines.flow.SharedFlow
import java.io.FileDescriptor

interface Recorder {

    val amplitude: SharedFlow<Int>

    fun setOutputFile(path: String)
    fun setOutputFile(fileDescriptor: FileDescriptor)
    fun prepare()
    suspend fun start()
    fun stop(): ByteArray
    fun resume()
    fun pause()
    fun release()
}