package com.danilovfa.feature.record.store

import android.content.Context
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.net.Uri
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.core.base.presentation.event.PermissionStatus
import com.danilovfa.core.library.log.LOG_TAG
import com.danilovfa.core.library.text.Text
import com.danilovfa.data.common.model.AudioData
import com.danilovfa.data.record.domain.repository.RecordRepository
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State
import com.danilovfa.feature.record.store.RecordStoreFactory.Msg
import com.danilovfa.libs.recorder.config.AudioRecordConfig
import com.danilovfa.libs.recorder.recorder.AudioRecorder
import com.danilovfa.libs.recorder.recorder.WavAudioRecorder
import com.danilovfa.libs.recorder.recorder.wav.WavHeader
import com.danilovfa.libs.recorder.source.DefaultAudioSource
import com.danilovfa.libs.recorder.writer.DefaultRecordWriter
import com.danilovfa.resources.drawable.strings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.util.UUID

internal class RecordStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()
    private var recorder: AudioRecorder? = null

    private var recorderJob: Job? = null

    override fun executeIntent(intent: Intent, getState: () -> State): Unit = when (intent) {
        Intent.OnRecordStartClicked -> onRecordClicked()
        is Intent.OnPermissionStatusChanged -> onPermissionStatusChanged(
            permissionStatus = intent.permissionStatus,
        )

        Intent.OnRecordStopClicked -> stopRecording()
        Intent.OnShowHelpDialogClicked -> publish(Label.ShowHelpDialog)
        Intent.OnImportRecordingClicked -> publish(Label.OpenFilePicker)
        is Intent.OnRecordImported -> importRecording(intent.uri, intent.context)
    }

    private fun onRecordClicked() {
        publish(Label.RequestAudioPermission)
    }

    private fun onPermissionStatusChanged(permissionStatus: PermissionStatus) =
        when (permissionStatus) {
            PermissionStatus.Denied -> publish(Label.ShowRationale)

            PermissionStatus.Granted -> {
                dispatch(Msg.UpdatePlaying(true))
                startRecording()
            }

            PermissionStatus.NeedsRationale -> publish(Label.ShowRationale)
        }

    private fun startRecording() {
        val filename = "${UUID.randomUUID()}.wav"
        val file = File(recordRepository.getRecordingsDir(), filename)

        val config = AudioRecordConfig.defaultConfig()
        val audioSource = DefaultAudioSource(audioRecordConfig = config)

        recorder = WavAudioRecorder(
            file = file,
            recordWriter = DefaultRecordWriter(
                audioSource = audioSource,
                amplitudeListener = {
                    Timber.tag(TAG).d("Amplitude: $it")
                    dispatch(Msg.AddAmplitude(it))
                }
            )
        )


        recorderJob?.cancel()
        recorderJob = scope.launch {
            launch {
                recorder?.startRecording()
            }

            startTimer {
                stopRecording()
                publish(
                    Label.Analyze(
                        getAudioData(
                            filename, config, audioSource.getBufferSize()
                        )
                    )
                )
            }
        }
    }

    private suspend fun startTimer(onCompleted: () -> Unit) {
        val startTime = Clock.System.now()
        dispatch(Msg.UpdateRecordingStartTime(startTime))

        var currentTime = Clock.System.now()

        while (currentTime.toEpochMilliseconds() - startTime.toEpochMilliseconds() <= END_TIME_MILLIS) {
            delay(TIMER_DELAY)
            currentTime = Clock.System.now()
        }

        onCompleted()
    }

    private fun stopRecording() {
        dispatch(Msg.UpdatePlaying(false))
        dispatch(Msg.UpdateRecordingStartTime(null))
        dispatch(Msg.UpdateAmplitudes(emptyList()))
        recorder?.stopRecording()
        recorderJob?.cancel()
    }

    private fun importRecording(uri: Uri, context: Context) {
        scope.launch {
            val fileType = uri.path?.let { path ->
                path.substring(path.lastIndexOf('.'))
            }

            if (fileType != ".wav") {
                publish(Label.ShowError(Text.Resource(strings.record_wrong_format)))
                return@launch
            }

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes().takeUnless { it.size < WavHeader.HEADER_SIZE_BYTES }
                    ?.let { bytes ->
                        recordRepository.saveEncodedRecording(bytes)
                            .onFailure {
                                publish(Label.ShowError(Text.Plain(it.message ?: "")))
                            }
                            .onSuccess { filename ->
                                val config = WavHeader.getConfigFromHeader(bytes.copyOfRange(0, 45))
                                val bufferSize = AudioRecord.getMinBufferSize(
                                    config.frequency,
                                    config.channel,
                                    config.audioEncoding
                                )

                                Timber.tag(LOG_TAG).d("Header: $config")

                                publish(Label.Analyze(getAudioData(filename, config, bufferSize)))
                            }
                    }
            }
        }
    }

    private fun getAudioData(
        filename: String,
        config: AudioRecordConfig,
        bufferSize: Int
    ): AudioData = AudioData(
        filename = filename,
        frequency = config.frequency,
        channel = when (config.channel) {
            CHANNEL_IN_MONO -> 1
            else -> 2
        },
        bitsPerSample = when (config.audioEncoding) {
            ENCODING_PCM_16BIT -> 16
            else -> 8
        },
        bufferSize = bufferSize
    )

    companion object {
        private const val TAG = "RecordStore"
        private const val TIMER_DELAY = 50L
        private const val END_TIME_MILLIS = 10000L
    }
}