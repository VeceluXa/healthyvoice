package com.danilovfa.presentation.analysis.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.domain.common.model.RecordingAnalysis
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Label
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State

internal class AnalyzeStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(
        recordingId: Long,
    ): AnalyzeStore = object : AnalyzeStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(
                recordingId = recordingId,
            ),
            bootstrapper = SimpleBootstrapper(Action.ProcessRecording, Action.ObserveAnalysis),
            executorFactory = ::AnalyzeStoreExecutor,
            reducer = reducer
        ) { }

    sealed class Msg {
        data class UpdateRecordingAnalysis(val analysis: RecordingAnalysis) : Msg()
        data class UpdateRecordingLoading(val isLoading: Boolean) : Msg()
        data class UpdateAnalysisLoading(val isLoading: Boolean) : Msg()
        data class UpdateAmplitudes(val amplitudes: List<Short>) : Msg()
    }

    sealed class Action {
        data object ProcessRecording : Action()
        data object ObserveAnalysis : Action()
    }

    private val reducer = Reducer<State, Msg> { msg ->
        when (msg) {
            is Msg.UpdateAmplitudes -> copy(amplitudes = msg.amplitudes)
            is Msg.UpdateRecordingLoading -> copy(isRecordingLoading = msg.isLoading)
            is Msg.UpdateAnalysisLoading -> copy(isAnalysisLoading = msg.isLoading)
            is Msg.UpdateRecordingAnalysis -> copy(recordingAnalysis = msg.analysis)
        }
    }

    companion object {
        private const val STORE_NAME = "AnalyzeStore"
    }
}