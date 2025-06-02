package com.danilovfa.presentation.patient.create.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.Sex
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Intent
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Label
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.State
import kotlinx.datetime.LocalDate

internal class PatientCreateStoreFactory(private val storeFactory: StoreFactory) {

    fun create(patient: Patient?): PatientCreateStore = object : PatientCreateStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(
                patient = patient
            ),
            executorFactory = ::PatientCreateStoreExecutor,
            reducer = reducer
        ) {}

    sealed class Msg {
        data class ChangeName(val name: String) : Msg()
        data class ChangeBirthDate(val birthDate: LocalDate) : Msg()
        data class ChangeSex(val sex: Sex) : Msg()
        data class ChangeNote(val note: String) : Msg()
        data class ChangeSaving(val isSaving: Boolean) : Msg()
    }

    private val reducer = Reducer<State, Msg> { msg ->
        when (msg) {
            is Msg.ChangeBirthDate -> copy(birthDate = msg.birthDate)
            is Msg.ChangeName -> copy(name = msg.name)
            is Msg.ChangeNote -> copy(note = msg.note)
            is Msg.ChangeSex -> copy(sex = msg.sex)
            is Msg.ChangeSaving -> copy(isSaving = msg.isSaving)
        }
    }

    companion object {
        private const val STORE_NAME = "PatientCreateStore"
    }
}