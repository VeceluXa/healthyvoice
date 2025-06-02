package com.danilovfa.export.presentation.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ExportRequestData {
    @Serializable
    data object AllPatients : ExportRequestData()

    @Serializable
    data class Patient(val patientId: Long) : ExportRequestData()

    @Serializable
    data class Analysis(val patientId: Long, val analysisId: Long) : ExportRequestData()
}