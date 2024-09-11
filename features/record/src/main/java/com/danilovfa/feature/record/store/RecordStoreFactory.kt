package com.danilovfa.feature.record.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State
import kotlinx.datetime.Instant

internal class RecordStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): RecordStore = object : RecordStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            bootstrapper = null,
            executorFactory = ::RecordStoreExecutor,
            reducer = reducer
        ) {}

    sealed class Msg {
        data class UpdatePlaying(val isPlaying: Boolean) : Msg()
        data class UpdateRecordingStartTime(val startTime: Instant?) : Msg()
        data class UpdateAmplitudes(val amplitudes: List<Int>) : Msg()
        data class AddAmplitude(val amplitude: Int) : Msg()
    }

    private val reducer = Reducer<State, Msg> { msg ->
        when (msg) {
            is Msg.UpdatePlaying -> copy(isRecording = msg.isPlaying)
            is Msg.UpdateRecordingStartTime -> copy(recordingStartTime = msg.startTime)
            is Msg.UpdateAmplitudes -> copy(amplitudes = msg.amplitudes)
            is Msg.AddAmplitude -> {
                copy(amplitudes = amplitudes + msg.amplitude)
            }
        }
    }

    companion object {
        private const val STORE_NAME = "RecordStore"
    }
}