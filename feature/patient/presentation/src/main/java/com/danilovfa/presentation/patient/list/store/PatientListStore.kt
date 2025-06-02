package com.danilovfa.presentation.patient.list.store

import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.common.base.permission.PermissionStatus
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.presentation.patient.list.store.PatientListStore.Intent
import com.danilovfa.presentation.patient.list.store.PatientListStore.Label
import com.danilovfa.presentation.patient.list.store.PatientListStore.State

internal interface PatientListStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object OnCreatePatientClicked : Intent()
        data class OnPatientClicked(val patient: Patient) : Intent()
        data object OnExportClicked : Intent()
        data object OnDeleteClicked : Intent()
        data class OnQueryChanged(val query: String) : Intent()
        data object RequestNotificationPermission : Intent()
        data class OnNotificationPermissionRequested(val permissionStatus: PermissionStatus) : Intent()
    }

    data class State(
        val patients: List<Patient> = emptyList(),
        val searchQuery: String = "",
        val patientSearched: List<Patient> = patients
    )

    sealed class Label {
        data object CreatePatient : Label()
        data class ShowPatientDetails(val patientId: Long) : Label()
        data object RequestNotificationPermission : Label()
        data object DismissRequestNotificationPermission : Label()
        data object ShowNotificationPermissionRationale : Label()
        data object ShowTodo : Label()
    }
}