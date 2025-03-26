package com.danilovfa.presentation.analyze.store

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.analyze.model.AnalyzeParametersUi
import com.danilovfa.presentation.analyze.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analyze.store.AnalyzeStore.Label
import com.danilovfa.presentation.analyze.store.AnalyzeStore.State

interface AnalyzeStore : Store<Intent, State, Label> {
    sealed class Intent {

        data object RetryAnalyze : Intent()
        data object OnBackClicked : Intent()
        data object OnBackConfirmed : Intent()
    }

    @Immutable
    data class State(
        val audioData: AudioData,
        val isLoading: Boolean = true,
        val parameters: AnalyzeParametersUi? = null,
        val amplitudes: List<Short> = emptyList()
    )

    sealed class Label {
        data object NavigateBack : Label()

        data object ShowConfirmNavigateBack : Label()

        data class ShowError(val text: Text? = null) : Label()
    }
}