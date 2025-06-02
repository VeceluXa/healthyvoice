package com.danilovfa.presentation.patient.detail.store

import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.domain.common.model.Analysis
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.RecordingAnalysis
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Intent
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Label
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.State

internal interface PatientDetailStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object OnBackClicked : Intent()
        data object OnExportClicked : Intent()
        data class OnAnalysisClicked(val analysis: RecordingAnalysis) : Intent()
        data object OnRecordClicked : Intent()
        data object OnEditPatientClicked : Intent()
        data class OnNoteChanged(val note: String) : Intent()
        data class OnSearchQueryChanged(val query: String) : Intent()
        data object OnDeletePatientClicked : Intent()
        data object ConfirmDeletePatient : Intent()
    }

    data class State(
        val patientId: Long,
        val patient: Patient? = null,
        val note: String = "",
        val analyzes: List<RecordingAnalysis> = emptyList(),
        val searchedAnalyzes: List<RecordingAnalysis> = emptyList(),
        val searchQuery: String = ""
    )

    sealed class Label {
        data object NavigateBack : Label()
        data object NewRecord : Label()
        data class EditPatient(val patient: Patient) : Label()
        data class ShowAnalysis(val recordingId: Long) : Label()
        data object ShowConfirmDeleteDialog : Label()
    }
}