package com.danilovfa.presentation.record.main.store

import android.content.Context
import android.net.Uri
import co.touchlab.kermit.Logger
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.common.base.permission.PermissionStatus
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.core.presentation.error.getErrorText
import com.danilovfa.common.resources.strings
import com.danilovfa.domain.record.repository.RecordRepository
import com.danilovfa.libs.recorder.config.AudioRecordConfig
import com.danilovfa.libs.recorder.recorder.AudioRecorder
import com.danilovfa.libs.recorder.recorder.WavAudioRecorder
import com.danilovfa.libs.recorder.recorder.wav.WavHeader
import com.danilovfa.libs.recorder.source.DefaultAudioSource
import com.danilovfa.libs.recorder.writer.DefaultRecordWriter
import com.danilovfa.presentation.record.main.store.RecordStore.Intent
import com.danilovfa.presentation.record.main.store.RecordStore.Label
import com.danilovfa.presentation.record.main.store.RecordStore.State
import com.danilovfa.presentation.record.main.store.RecordStoreFactory.Msg
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class RecordStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()
    private var recorder: AudioRecorder? = null

    private var recorderJob: Job? = null

    /**
     * Needed because compose calls permission change on screen creation
     */
    private var hasRecordClicked: Boolean = false

    override fun executeIntent(intent: Intent, getState: () -> State): Unit = when (intent) {
        Intent.OnBackClicked -> publish(Label.NavigateBack)
        Intent.OnRecordStartClicked -> onRecordClicked()
        is Intent.OnPermissionStatusChanged -> onPermissionStatusChanged(
            patientId = getState().patientId,
            permissionStatus = intent.permissionStatus,
        )

        Intent.OnRecordStopClicked -> stopRecording()
        Intent.OnShowHelpDialogClicked -> publish(Label.ShowHelpDialog)
        Intent.OnImportRecordingClicked -> publish(Label.OpenFilePicker)
        is Intent.OnRecordImported -> importRecording(getState().patientId, intent.uri, intent.context)
    }

    private fun onRecordClicked() {
        hasRecordClicked = true
        publish(Label.RequestAudioPermission)
    }

    private fun onPermissionStatusChanged(patientId: Long, permissionStatus: PermissionStatus) =
        when (permissionStatus) {
            is PermissionStatus.Denied -> publish(Label.ShowRationale)

            is PermissionStatus.Granted -> {
                if (hasRecordClicked) {
                    dispatch(Msg.UpdatePlaying(true))
                    startRecording(patientId)
                }
                Unit
            }

            is PermissionStatus.NeedsRationale -> publish(Label.ShowRationale)
        }

    private fun startRecording(patientId: Long) {
        scope.launch {
            hasRecordClicked = false
            val recording = recordRepository.start(patientId)

            val file = recording.file
            val config = AudioRecordConfig.defaultConfig()
            val audioSource = DefaultAudioSource(audioRecordConfig = config)

            recorder = WavAudioRecorder(
                file = file,
                recordWriter = DefaultRecordWriter(
                    audioSource = audioSource,
                    amplitudeListener = {
                        Logger.withTag(TAG).d("Amplitude: $it")
                        dispatch(Msg.AddAmplitude(it))
                    }
                )
            )

            recorderJob?.cancel()
            recorderJob = launch {
                recorder?.startRecording()
            }

            startTimer()
            stopRecording()

            recordRepository.endRecording(recording)
                .onSuccess { publish(Label.Analyze(it.id)) }
                .onFailure { publish(Label.ShowError(it.getErrorText())) }
        }
    }

    private suspend fun startTimer() {
        val startTime = Clock.System.now()
        dispatch(Msg.UpdateRecordingStartTime(startTime))

        var currentTime = Clock.System.now()

        while (currentTime.toEpochMilliseconds() - startTime.toEpochMilliseconds() <= END_TIME_MILLIS) {
            delay(TIMER_DELAY)
            currentTime = Clock.System.now()
        }
    }

    private fun stopRecording() {
        dispatch(Msg.UpdatePlaying(false))
        dispatch(Msg.UpdateRecordingStartTime(null))
        dispatch(Msg.UpdateAmplitudes(emptyList()))
        recorder?.stopRecording()
        recorderJob?.cancel()
    }

    private fun importRecording(patientId: Long, uri: Uri, context: Context) {
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
                        recordRepository.importRecording(patientId, bytes)
                            .onFailure {
                                publish(Label.ShowError(Text.Plain(it.message ?: "")))
                            }
                            .onSuccess { recording ->
                                publish(Label.Analyze(recording.id))
                            }
                    }
            }
        }
    }

    companion object {
        private const val TAG = "RecordStore"
        private const val TIMER_DELAY = 50L
        private const val END_TIME_MILLIS = 6_000L
    }
}