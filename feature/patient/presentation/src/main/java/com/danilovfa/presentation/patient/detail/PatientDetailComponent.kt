package com.danilovfa.presentation.patient.detail

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Intent
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.State

internal interface PatientDetailComponent : StatefulComponent<Intent, State> {
    sealed class Output {
        data class NavigateRecord(val patientId: Long) : Output()
        data class NavigateEdit(val patient: Patient) : Output()
        data class NavigateAnalysis(val recordingId: Long) : Output()
        data object NavigateBack : Output()
    }
}