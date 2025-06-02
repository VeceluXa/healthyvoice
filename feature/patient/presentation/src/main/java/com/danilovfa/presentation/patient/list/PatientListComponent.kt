package com.danilovfa.presentation.patient.list

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.common.base.event.Event
import com.danilovfa.presentation.patient.list.store.PatientListStore.Intent
import com.danilovfa.presentation.patient.list.store.PatientListStore.State

internal interface PatientListComponent : StatefulComponent<Intent, State> {
    sealed class PatientListEvents : Event {
        data object LaunchAppSettings : PatientListEvents()
    }

    sealed class Output {
        data object NavigatePatientCreate : Output()
        data class NavigatePatientDetails(val patientId: Long) : Output()
    }
}