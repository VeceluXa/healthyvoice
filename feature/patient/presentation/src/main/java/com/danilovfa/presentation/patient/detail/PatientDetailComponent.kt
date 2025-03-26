package com.danilovfa.presentation.patient.detail

interface PatientDetailComponent {
    sealed class Output {
        data class NavigateRecord(val patientId: Long) : Output()
        data class NavigateAnalysis(val analysisId: Long) : Output()
        data object NavigateBack : Output()
    }
}