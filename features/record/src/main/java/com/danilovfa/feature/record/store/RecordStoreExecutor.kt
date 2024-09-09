package com.danilovfa.feature.record.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.core.base.presentation.event.PermissionStatus
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State
import com.danilovfa.feature.record.store.RecordStoreFactory.Msg
import com.danilovfa.libs.recorder.recorder.Recorder
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.concurrent.timer

internal class RecordStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

    private val recorder: Recorder by inject()

    private var timerJob: Job? = null

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        Intent.OnRecordClicked -> onRecordClicked(getState().isRecording)
        is Intent.OnPermissionStatusChanged -> onPermissionStatusChanged(
            permissionStatus = intent.permissionStatus,
            initialTime = getState().recordingTimeMillis
        )
    }

    private fun onRecordClicked(isRecording: Boolean) {
        if (isRecording) {
            pauseRecording()
        } else {
            publish(Label.RequestAudioPermission)
        }
    }

    private fun onPermissionStatusChanged(
        permissionStatus: PermissionStatus,
        initialTime: Long,
    ) =
        when (permissionStatus) {
            PermissionStatus.Denied -> publish(Label.ShowRationale)

            PermissionStatus.Granted -> {
                dispatch(Msg.UpdatePlaying(true))
                startRecording(initialTime)
            }

            PermissionStatus.NeedsRationale -> publish(Label.ShowRationale)
        }

    private fun pauseRecording() {
        dispatch(Msg.UpdatePlaying(false))
        timerJob?.cancel()
        recorder.pause()
    }

    private fun startRecording(initialTime: Long) {
        timerJob = scope.launch {
            var time = initialTime
            recorder.start()
            while (time <= END_TIME_MILLIS) {
                delay(TIMER_DELAY)
                time += TIMER_DELAY
                dispatch(Msg.AddAmplitude(recorder.getMaxAmplitude()))
                dispatch(Msg.UpdateRecordingTime(time))
            }
            dispatch(Msg.UpdatePlaying(false))
            dispatch(Msg.UpdateRecordingTime(0))
        }
    }

    companion object {
        private const val TIMER_DELAY = 5L
        private const val END_TIME_MILLIS = 5000L
    }
}