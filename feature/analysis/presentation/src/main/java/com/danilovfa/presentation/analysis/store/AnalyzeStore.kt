package com.danilovfa.presentation.analysis.store

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.domain.common.model.RecordingAnalysis
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.analysis.model.ParameterDataUi
import com.danilovfa.presentation.analysis.model.toParametersData
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Label
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State

interface AnalyzeStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object RetryAnalyze : Intent()
        data object OnBackClicked : Intent()
        data object OnExportClicked : Intent()
        data object OnDeleteClicked : Intent()
    }

    @Immutable
    data class State(
        val recordingId: Long,
        val recordingAnalysis: RecordingAnalysis? = null,
        val isRecordingLoading: Boolean = true,
        val isAnalysisLoading: Boolean = true,
        val amplitudes: List<Short> = emptyList()
    ) {
        val parameters get() = recordingAnalysis?.analysis?.toParametersData().orEmpty()
    }

    sealed class Label {
        data object NavigateBack : Label()
        data class ShowError(val text: Text? = null) : Label()
        data object ShowTodo : Label()
    }
}