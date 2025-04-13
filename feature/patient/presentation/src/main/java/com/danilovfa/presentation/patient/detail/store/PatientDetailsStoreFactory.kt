package com.danilovfa.presentation.patient.detail.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create
import com.danilovfa.domain.common.model.Analysis
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.RecordingAnalysis
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Intent
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Label
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.State

internal class PatientDetailsStoreFactory(private val storeFactory: StoreFactory) {

    fun create(patientId: Long): PatientDetailStore = object : PatientDetailStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(patientId = patientId),
            bootstrapper = SimpleBootstrapper(Action.ObservePatient, Action.ObserveAnalyzes),
            executorFactory = ::PatientDetailsStoreExecutor,
            reducer = reducer
        ) {}

    sealed class Action {
        data object ObservePatient : Action()
        data object ObserveAnalyzes : Action()
    }

    sealed class Msg {
        data class ChangePatient(val patient: Patient) : Msg()
        data class ChangeAnalyzes(val analyzes: List<RecordingAnalysis>) : Msg()
        data class ChangeNote(val note: String) : Msg()
        data class ChangeSearchQuery(val query: String) : Msg()
        data class ChangeSearchedAnalyzes(val analyzes: List<RecordingAnalysis>) : Msg()
    }

    private val reducer = Reducer<State, Msg> { msg ->
        when (msg) {
            is Msg.ChangeNote -> copy(note = msg.note)
            is Msg.ChangeSearchQuery -> copy(searchQuery = msg.query)
            is Msg.ChangeSearchedAnalyzes -> copy(searchedAnalyzes = msg.analyzes)
            is Msg.ChangeAnalyzes -> copy(analyzes = msg.analyzes)
            is Msg.ChangePatient -> copy(patient = msg.patient)
        }
    }

    companion object {
        private const val STORE_NAME = "PatientDetailsStore"
    }
}