package com.danilovfa.presentation.patient.create.store

import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.Sex
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Intent
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Label
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.State
import kotlinx.datetime.LocalDate

internal interface PatientCreateStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object OnBackClicked : Intent()
        data class OnNameChanged(val name: String) : Intent()
        data class OnBirthDateChanged(val birthDate: LocalDate) : Intent()
        data class OnAddressChanged(val address: String) : Intent()
        data class OnSexChanged(val sex: Sex) : Intent()
        data class OnNoteChanged(val note: String) : Intent()
        data object OnSaveClicked : Intent()
    }

    data class State(
        val patient: Patient?,
        val name: String = patient?.name ?: "",
        val birthDate: LocalDate? = patient?.birthDate,
        val address: String = patient?.address ?: "",
        val sex: Sex = patient?.sex ?: Sex.MALE,
        val note: String = patient?.note ?: "",
        val isSaving: Boolean = false
    ) {
        val isEdit get() = patient != null

        val canSave get() = areAllFieldsFilled() && (isEdit.not() || isDataChanged())

        private fun isDataChanged(): Boolean =
            patient != null && (patient.name != name || patient.address != address ||
                    patient.birthDate != birthDate || patient.sex != sex || patient.note != note)

        private fun areAllFieldsFilled(): Boolean =
            name.isNotEmpty() && birthDate != null && address.isNotEmpty()
    }

    sealed class Label {
        data object NavigateBack : Label()
        data class NavigatePatient(val patientId: Long) : Label()
    }
}