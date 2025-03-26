package com.danilovfa.presentation.patient.create

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Intent
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.State

internal interface PatientCreateComponent : StatefulComponent<Intent, State> {
    sealed class Output {
        data object NavigateBack : Output()
        data class NavigatePatient(val patientId: Long) : Output()
    }
}