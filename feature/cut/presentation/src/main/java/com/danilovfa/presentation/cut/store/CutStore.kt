package com.danilovfa.presentation.cut.store

import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.cut.store.CutStore.Intent
import com.danilovfa.presentation.cut.store.CutStore.Label
import com.danilovfa.presentation.cut.store.CutStore.State

interface CutStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object RetryLoadRecording : Intent()
        data class OnStartOffsetMoved(val startOffset: Int) : Intent()
        data class OnEndOffsetMoved(val endOffset: Int) : Intent()
        data object OnAnalyzeClicked : Intent()
        data object OnBackClicked : Intent()
        data object OnBackConfirmed : Intent()
    }

    data class State(
        val data: AudioData,
        val amplitudes: List<Float> = emptyList(),
        val startOffset: Int = data.audioCut?.startMillis ?: START_OFFSET_MILLIS,
        val endOffset: Int = data.audioCut?.endMillis ?: END_OFFSET_MILLIS
    )

    sealed class Label {
        data object ShowLoadRecordingError : Label()
        data object ShowBackConfirmationDialog : Label()
        data object NavigateBack : Label()
        data class NavigateAnalyze(val data: AudioData) : Label()
    }

    companion object {
        private const val START_OFFSET_MILLIS = 1000
        private const val END_OFFSET_MILLIS = 5000
    }
}