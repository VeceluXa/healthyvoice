package com.danilovfa.presentation.analysis.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Label
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State

internal class AnalyzeStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(audioData: AudioData): AnalyzeStore = object : AnalyzeStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(audioData = audioData),
            bootstrapper = SimpleBootstrapper(Action.ProcessRecording),
            executorFactory = ::AnalyzeStoreExecutor,
            reducer = reducer
        ) { }

    sealed class Msg {
        data class UpdateLoading(val isLoading: Boolean) : Msg()
        data class UpdateAmplitudes(val amplitudes: List<Short>) : Msg()
        data class UpdateParameters(val parameters: AnalyzeParametersUi) : Msg()
    }

    sealed class Action {
        data object ProcessRecording : Action()
    }

    private val reducer = Reducer<State, Msg> { msg ->
        when (msg) {
            is Msg.UpdateAmplitudes -> copy(amplitudes = msg.amplitudes)
            is Msg.UpdateParameters -> copy(parameters = msg.parameters)
            is Msg.UpdateLoading -> copy(isLoading = msg.isLoading)
        }
    }

    companion object {
        private const val STORE_NAME = "AnalyzeStore"
    }
}