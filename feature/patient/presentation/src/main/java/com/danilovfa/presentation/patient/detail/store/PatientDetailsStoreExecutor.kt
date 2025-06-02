package com.danilovfa.presentation.patient.detail.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.common.core.domain.time.KotlinDateTimeFormatters.dateTime
import com.danilovfa.common.core.domain.time.format
import com.danilovfa.domain.analysis.AnalysisRepository
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.RecordingAnalysis
import com.danilovfa.domain.patient.repository.PatientRepository
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Intent
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Label
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.State
import com.danilovfa.presentation.patient.detail.store.PatientDetailsStoreFactory.Action
import com.danilovfa.presentation.patient.detail.store.PatientDetailsStoreFactory.Msg
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class PatientDetailsStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    private var searchJob: Job? = null
    private val searchStateFlow = MutableStateFlow("")

    private var noteJob: Job? = null
    private val noteStateFlow = MutableStateFlow("")

    private val repository: PatientRepository by inject()
    private val analysisRepository: AnalysisRepository by inject()

    override fun executeAction(action: Action, getState: () -> State) = when (action) {
        Action.ObservePatient -> observePatient(getState().patientId)
        Action.ObserveAnalyzes -> observeAnalyzes(getState().patientId)
    }

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        is Intent.OnAnalysisClicked -> publish(
            Label.ShowAnalysis(
                recordingId = intent.analysis.recording.id
            )
        )

        Intent.OnBackClicked -> publish(Label.NavigateBack)
        Intent.OnEditPatientClicked -> onEditClicked(getState().patient)
        Intent.OnExportClicked -> publish(Label.ExportPatient)
        is Intent.OnNoteChanged -> onNoteChanged(intent.note)
        Intent.OnRecordClicked -> publish(Label.NewRecord)
        is Intent.OnSearchQueryChanged -> onQueryChanged(intent.query)
        Intent.ConfirmDeletePatient -> deletePatient(getState().patientId)
        Intent.OnDeletePatientClicked -> publish(Label.ShowConfirmDeleteDialog)
    }

    private fun observePatient(patientId: Long) {
        repository.observePatient(patientId)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { patient ->
                dispatch(Msg.ChangePatient(patient))
                onNoteChanged(patient.note)
            }
            .launchIn(scope)

        observeNote(patientId)
    }

    private fun observeAnalyzes(patientId: Long) {
        analysisRepository.observePatientRecordingAnalyzes(patientId)
            .distinctUntilChanged()
            .map { analyzes ->
                analyzes
                    .sortedBy { it.recording.timestamp }
                    .reversed()
            }
            .onEach { analyzes ->
                dispatch(Msg.ChangeAnalyzes(analyzes))
                observeSearchQuery(analyzes)
            }
            .launchIn(scope)
    }

    private fun observeSearchQuery(analyzes: List<RecordingAnalysis>) {
        searchJob?.cancel()
        searchJob = searchStateFlow
            .debounce(SEARCH_DEBOUNCE)
            .onEach { query ->
                if (query.isNotEmpty()) {
                    searchAnalyzes(query, analyzes)
                } else {
                    dispatch(Msg.ChangeSearchedAnalyzes(analyzes))
                }
            }
            .launchIn(scope)
    }

    private fun observeNote(patientId: Long) {
        noteJob?.cancel()
        noteJob = noteStateFlow
            .debounce(NOTE_SAVE_DEBOUNCE)
            .onEach { note ->
                repository.updateNote(patientId, note)
            }
            .launchIn(scope)
    }

    private fun searchAnalyzes(query: String, analyzes: List<RecordingAnalysis>) {
        val optimizedQuery = query.lowercase().trim()

        val searchedAnalyzes = analyzes.filter {
            it.recording.timestamp.toLocalDateTime(TimeZone.UTC).format { dateTime() }.lowercase()
                .contains(optimizedQuery)
        }

        dispatch(Msg.ChangeSearchedAnalyzes(searchedAnalyzes))
    }

    private fun onQueryChanged(query: String) {
        dispatch(Msg.ChangeSearchQuery(query))
        searchStateFlow.update { query }
    }

    private fun onNoteChanged(note: String) {
        dispatch(Msg.ChangeNote(note))
        noteStateFlow.update { note }
    }

    private fun onEditClicked(patient: Patient?) {
        patient?.let { publish(Label.EditPatient(it)) }
    }

    private fun deletePatient(patientId: Long) {
        scope.launch {
            repository.deletePatient(patientId)
            publish(Label.NavigateBack)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE = 300L
        private const val NOTE_SAVE_DEBOUNCE = 1_000L
    }
}