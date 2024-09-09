package com.danilovfa.libs.recorder.recorder

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.danilovfa.libs.recorder.utils.Constants
import kotlinx.coroutines.Dispatchers
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
    private val sampleRate: Int = Constants.SAMPLE_RATE,
    private val channel: Int = Constants.CHANNEL,
    private val audioFormat: Int = Constants.AUDIO_FORMAT
) : Recorder {

    private var buffer: ByteArray = ByteArray(0)
    private var isPaused = AtomicBoolean(false)
    private var isStopped = AtomicBoolean(false)
    private var amplitude = AtomicInteger(0)

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
        minBufferSize * Constants.DURATION
    )

    override fun setOutputFile(path: String) {
        this.outputFile = path
    }

    override fun setOutputFile(fileDescriptor: FileDescriptor) {
        this.fileDescriptor = fileDescriptor
    }

    override fun prepare() {  }

    override suspend fun start() = withContext(Dispatchers.IO) {
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

                updateAmplitude(rawData)
            }
        }
    }

    override fun stop() {
        isPaused.set(true)
        isStopped.set(true)
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

    override fun getMaxAmplitude(): Int {
        return amplitude.get()
    }

    private fun updateAmplitude(data: ShortArray) {
        var sum = 0L
        for (i in 0 until minBufferSize step 2) {
            sum += abs(data[i].toInt())
        }
        amplitude.set((sum / (minBufferSize / 8)).toInt())
    }

    companion object {
        private const val TAG = "Mp3Recorder"
    }
}