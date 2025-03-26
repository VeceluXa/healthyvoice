package com.danilovfa.presentation.cut.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.cut.store.CutStore.Intent
import com.danilovfa.presentation.cut.store.CutStore.Label
import com.danilovfa.presentation.cut.store.CutStore.State

internal class CutStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(data: AudioData): CutStore = object : CutStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(data = data),
            bootstrapper = SimpleBootstrapper(Action.LoadRecording),
            executorFactory = ::CutStoreExecutor,
            reducer = reducer
        ) {}

    sealed class Msg {
        data class UpdateAmplitudes(val amplitudes: List<Float>) : Msg()
        data class UpdateStartOffset(val startOffset: Int) : Msg()
        data class UpdateEndOffset(val endOffset: Int) : Msg()
    }

    sealed class Action {
        data object LoadRecording : Action()
    }

    private val reducer = Reducer<State, Msg> { msg ->
        when (msg) {
            is Msg.UpdateAmplitudes -> copy(amplitudes = msg.amplitudes)
            is Msg.UpdateEndOffset -> copy(endOffset = msg.endOffset)
            is Msg.UpdateStartOffset -> copy(startOffset = msg.startOffset)
        }
    }

    companion object {
        private const val STORE_NAME = "CutStore"
    }
}