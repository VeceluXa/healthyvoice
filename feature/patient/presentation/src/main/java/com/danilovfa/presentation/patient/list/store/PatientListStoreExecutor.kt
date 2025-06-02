package com.danilovfa.presentation.patient.list.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.common.base.permission.PermissionStatus
import com.danilovfa.common.core.domain.time.KotlinDateTimeFormatters.date
import com.danilovfa.common.core.domain.time.format
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.patient.repository.PatientRepository
import com.danilovfa.export.presentation.ExportWorkFactory
import com.danilovfa.export.presentation.model.ExportRequestData
import com.danilovfa.presentation.patient.list.store.PatientListStore.Intent
import com.danilovfa.presentation.patient.list.store.PatientListStore.Label
import com.danilovfa.presentation.patient.list.store.PatientListStore.State
import com.danilovfa.presentation.patient.list.store.PatientListStoreFactory.Action
import com.danilovfa.presentation.patient.list.store.PatientListStoreFactory.Msg
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class PatientListStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    private var searchJob: Job? = null
    private val searchStateFlow = MutableStateFlow("")

    private val repository: PatientRepository by inject()
    private val exportWorkFactory: ExportWorkFactory by inject()

    override fun executeAction(action: Action, getState: () -> State) = when (action) {
        Action.ObservePatients -> observePatients()
    }

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        Intent.OnCreatePatientClicked -> publish(Label.CreatePatient)
        Intent.OnExportClicked -> export()
        is Intent.OnPatientClicked -> publish(Label.ShowPatientDetails(intent.patient.id))
        is Intent.OnQueryChanged -> onSearchQueryChanged(intent.query)
        is Intent.OnNotificationPermissionRequested -> onPermissionRequested(intent.permissionStatus)
        Intent.RequestNotificationPermission -> publish(Label.RequestNotificationPermission)
        Intent.OnDeleteClicked -> publish(Label.ShowTodo)
    }

    private fun observePatients() {
        repository.observePatients()
            .onEach { patients ->
                dispatch(Msg.ChangePatients(patients))
                observeSearchQuery(patients)
            }
            .launchIn(scope)
    }

    private fun observeSearchQuery(patients: List<Patient>) {
        searchJob?.cancel()
        searchJob = searchStateFlow
            .debounce(SEARCH_DEBOUNCE)
            .onEach { query ->
                if (query.isNotEmpty()) {
                    searchPatients(patients, query)
                } else {
                    dispatch(Msg.ChangeSearchedPatients(patients))
                }
            }
            .launchIn(scope)
    }

    private fun onSearchQueryChanged(query: String) {
        dispatch(Msg.ChangeSearchQuery(query))
        searchStateFlow.update { query }
    }

    private fun searchPatients(patients: List<Patient>, query: String) {
        val lcQuery = query.lowercase().trim()

        val searchedPatients = patients.filter { patient ->
            patient.name.lowercase().contains(lcQuery) ||
                    patient.birthDate.format { date() }.contains(lcQuery) ||
                    patient.note.lowercase().contains(lcQuery)
        }

        dispatch(Msg.ChangeSearchedPatients(searchedPatients))
    }

    private fun export() {
        exportWorkFactory.create(ExportRequestData.AllPatients)
    }

    private fun onPermissionRequested(permissionStatus: PermissionStatus) {
        when (permissionStatus) {
            is PermissionStatus.Denied -> publish(Label.ShowNotificationPermissionRationale)
            is PermissionStatus.Granted -> publish(Label.DismissRequestNotificationPermission)
            is PermissionStatus.NeedsRationale -> publish(Label.ShowNotificationPermissionRationale)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE = 300L
    }
}