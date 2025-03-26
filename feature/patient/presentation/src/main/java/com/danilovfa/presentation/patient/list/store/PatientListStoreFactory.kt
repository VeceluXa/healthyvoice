package com.danilovfa.presentation.patient.list.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.presentation.patient.list.store.PatientListStore.Intent
import com.danilovfa.presentation.patient.list.store.PatientListStore.Label
import com.danilovfa.presentation.patient.list.store.PatientListStore.State

internal class PatientListStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): PatientListStore = object : PatientListStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.ObservePatients),
            executorFactory = ::PatientListStoreExecutor,
            reducer = reducer
        ) {}

    sealed class Action {
        data object ObservePatients : Action()
    }

    sealed class Msg {
        data class ChangePatients(val patients: List<Patient>) : Msg()
        data class ChangeSearchQuery(val query: String) : Msg()
        data class ChangeSearchedPatients(val patients: List<Patient>) : Msg()
    }

    private val reducer = Reducer<State, Msg> { msg ->
        when (msg) {
            is Msg.ChangePatients -> copy(patients = msg.patients)
            is Msg.ChangeSearchQuery -> copy(searchQuery = msg.query)
            is Msg.ChangeSearchedPatients -> copy(patientSearched = msg.patients)
        }
    }

    companion object {
        private const val STORE_NAME = "PatientListStore"
    }
}