package com.danilovfa.feature.record.store

import android.util.Log
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.core.base.presentation.event.PermissionStatus
import com.danilovfa.core.library.flow.throttleLatest
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State
import com.danilovfa.feature.record.store.RecordStoreFactory.Msg
import com.danilovfa.libs.recorder.recorder.Recorder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class RecordStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

    private val recorder: Recorder by inject()

    private var timerJob: Job? = null
    private var amplitudeJob: Job? = null

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        Intent.OnRecordStartClicked -> onRecordClicked()
        is Intent.OnPermissionStatusChanged -> onPermissionStatusChanged(
            permissionStatus = intent.permissionStatus,
            initialTime = getState().recordingStartTime
        )

        Intent.OnRecordStopClicked -> stopRecording()
        Intent.OnShowHelpDialogClicked -> publish(Label.ShowHelpDialog)
    }

    private fun onRecordClicked() {
        publish(Label.RequestAudioPermission)
    }

    private fun onPermissionStatusChanged(
        permissionStatus: PermissionStatus,
        initialTime: Instant?,
    ) =
        when (permissionStatus) {
            PermissionStatus.Denied -> publish(Label.ShowRationale)

            PermissionStatus.Granted -> {
                dispatch(Msg.UpdatePlaying(true))
                startRecording(initialTime)
            }

            PermissionStatus.NeedsRationale -> publish(Label.ShowRationale)
        }

    private fun startRecording(initialTime: Instant?) {
        timerJob = scope.launch {
            launch { recorder.start() }
            observeAmplitudes()
            startTimer(initialTime)
        }
    }

    private fun observeAmplitudes() {
        amplitudeJob?.cancel()
        dispatch(Msg.UpdateAmplitudes(emptyList()))

        amplitudeJob = recorder.amplitude
//            .throttleLatest(AMPLITUDE_DEBOUNCE)
            .onEach {
                Log.d("RecorderGraph", "Amplitude: $it")
                dispatch(Msg.AddAmplitude(it))
            }
            .launchIn(scope)
    }

    private suspend fun startTimer(initialTime: Instant?) {
        val startTime = if (initialTime == null) {
            val time = Clock.System.now()
            dispatch(Msg.UpdateRecordingStartTime(time))
            time
        } else initialTime

        var currentTime = Clock.System.now()

//        while (currentTime.toEpochMilliseconds() - startTime.toEpochMilliseconds() <= END_TIME_MILLIS) {
//            delay(TIMER_DELAY)
//            currentTime = Clock.System.now()
//        }
//
//        stopRecording()
    }

    private fun stopRecording() {
        timerJob?.cancel()
        recorder.stop()
        dispatch(Msg.UpdatePlaying(false))
        dispatch(Msg.UpdateRecordingStartTime(null))
        dispatch(Msg.UpdateAmplitudes(emptyList()))
    }

    companion object {
        private const val AMPLITUDE_DEBOUNCE = 150L
        private const val TIMER_DELAY = 50L
        private const val END_TIME_MILLIS = 5000L
    }
}