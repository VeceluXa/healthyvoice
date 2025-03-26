package com.danilovfa.presentation.patient.create.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.patient.repository.PatientRepository
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Intent
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Label
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.State
import com.danilovfa.presentation.patient.create.store.PatientCreateStoreFactory.Msg
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class PatientCreateStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

    private val repository: PatientRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        is Intent.OnAddressChanged -> dispatch(Msg.ChangeAddress(intent.address))
        Intent.OnBackClicked -> publish(Label.NavigateBack)
        is Intent.OnBirthDateChanged -> dispatch(Msg.ChangeBirthDate(intent.birthDate))
        is Intent.OnNameChanged -> dispatch(Msg.ChangeName(intent.name))
        is Intent.OnNoteChanged -> dispatch(Msg.ChangeNote(intent.note))
        is Intent.OnSexChanged -> dispatch(Msg.ChangeSex(intent.sex))
        Intent.OnSaveClicked -> savePatient(getState())
    }

    private fun savePatient(state: State) {
        scope.launch {
            dispatch(Msg.ChangeSaving(true))
            if (state.patient != null) {
                editPatient(state)
            } else {
                createPatient(state)
            }
            dispatch(Msg.ChangeSaving(false))
        }
    }

    private suspend fun createPatient(state: State) {
        if (state.birthDate == null) return

        val patientId = repository.createPatient(
            Patient(
                name = state.name,
                birthDate = state.birthDate,
                address = state.address,
                sex = state.sex,
                note = state.note
            )
        )

        publish(Label.NavigatePatient(patientId))
    }

    private suspend fun editPatient(state: State) {
        if (state.patient == null || state.birthDate == null) return

        val transformedPatient = Patient(
            id = state.patient.id,
            name = state.name,
            birthDate = state.birthDate,
            address = state.address,
            sex = state.sex,
            note = state.note
        )

        repository.updatePatient(transformedPatient)
        publish(Label.NavigateBack)
    }
}