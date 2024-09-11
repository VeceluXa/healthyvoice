package com.danilovfa.libs.recorder.recorder

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.danilovfa.libs.recorder.utils.RecorderConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

class Mp3Recorder(
    private val context: Context,
    private val sampleRate: Int = RecorderConstants.SAMPLE_RATE,
    private val channel: Int = RecorderConstants.CHANNEL,
    private val audioFormat: Int = RecorderConstants.AUDIO_FORMAT
) : Recorder {

    private var buffer: ByteArray = ByteArray(0)
    private var isPaused = AtomicBoolean(false)
    private var isStopped = AtomicBoolean(false)
    private val _amplitude = MutableSharedFlow<Int>(0)
    override val amplitude = _amplitude.asSharedFlow()

    private var outputStream: FileOutputStream? = null

    private var outputFile: String? = null
    private var fileDescriptor: FileDescriptor? = null

    private val minBufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        channel,
        audioFormat
    )

    @SuppressLint("MissingPermission")
    private val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.CAMCORDER,
        sampleRate,
        channel,
        audioFormat,
        minBufferSize * RecorderConstants.DURATION
    )

    override fun setOutputFile(path: String) {
        this.outputFile = path
    }

    override fun setOutputFile(fileDescriptor: FileDescriptor) {
        this.fileDescriptor = fileDescriptor
    }

    override fun prepare() {  }

    override suspend fun start() = withContext(Dispatchers.IO) {
        isStopped.set(false)
        isPaused.set(false)

        val rawData = ShortArray(minBufferSize)
        buffer = ByteArray((7200 * rawData.size * 2 * 1.25).toInt())

        outputStream = try {
            if (fileDescriptor != null) {
                FileOutputStream(fileDescriptor)
            } else if (outputFile != null) {
                FileOutputStream(outputFile?.let { File(it) })
            } else null
        } catch (e: FileNotFoundException) {
            Timber.tag(TAG).e(e, "Couldn't open output stream")
            return@withContext
        }

        try {
            audioRecord.startRecording()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Couldn't start recording")
            return@withContext
        }

        while (!isStopped.get()) {
            if (!isPaused.get()) {
                val count = audioRecord.read(rawData, 0, minBufferSize)

                Log.d("Mp3Recorder", "Count: $count")
                updateAmplitude(rawData)
            }
        }
    }

    override fun stop() {
        audioRecord.stop()
        isPaused.set(true)
        isStopped.set(true)
        outputStream?.close()
    }

    override fun resume() {
        isPaused.set(false)
    }

    override fun pause() {
        isPaused.set(true)
    }

    override fun release() {
        outputStream?.close()
    }

    private suspend fun updateAmplitude(data: ShortArray) {
        var sum = 0L
        for (i in 0 until minBufferSize step 2) {
            sum += abs(data[i].toInt())
        }
        val newAmplitude = sum.toInt() / (minBufferSize / 8)
        Log.d("Mp3Recorder", "Amplitude: $newAmplitude")
        _amplitude.emit(newAmplitude)
    }

    companion object {
        private const val TAG = "Mp3Recorder"
    }
}