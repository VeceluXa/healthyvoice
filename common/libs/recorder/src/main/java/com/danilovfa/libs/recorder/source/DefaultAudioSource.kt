package com.danilovfa.libs.recorder.source

import android.annotation.SuppressLint
import android.media.AudioRecord
import com.danilovfa.libs.recorder.config.AudioRecordConfig

@SuppressLint("MissingPermission")
open class DefaultAudioSource(
    var audioRecordConfig: AudioRecordConfig = AudioRecordConfig.defaultConfig()
) : AudioSource {

    /**
     * backing property name for [getBufferSize]
     */
    private val _bufferSize: Int by lazy {
        AudioRecord.getMinBufferSize(
            audioRecordConfig.frequency,
            audioRecordConfig.channel,
            audioRecordConfig.audioEncoding
        )
    }

    private val _audioRecord: AudioRecord by lazy {
        AudioRecord(
            audioRecordConfig.audioSource,
            audioRecordConfig.frequency,
            audioRecordConfig.channel,
            audioRecordConfig.audioEncoding,
            getBufferSize()
        )
    }

    private var _isRecordAvailable: Boolean = false


    override fun getAudioRecord(): AudioRecord = _audioRecord

    override fun getAudioConfig(): AudioRecordConfig = audioRecordConfig

    override fun getBufferSize(): Int = _bufferSize

    override fun isRecordAvailable(): Boolean = _isRecordAvailable

    override fun setRecordAvailable(available: Boolean): AudioSource = this.apply {
        _isRecordAvailable = available
    }

    override fun preProcessAudioRecord(): AudioRecord = getAudioRecord().apply {
        startRecording()
        this@DefaultAudioSource.setRecordAvailable(true)
    }
}