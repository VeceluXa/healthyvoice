package com.danilovfa.presentation.patient.create

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.common.base.component.stateful.StatefulDefaultComponent
import com.danilovfa.common.base.utils.decompose.onBackClicked
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.presentation.patient.create.PatientCreateComponent.Output
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Intent
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Label
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.State
import com.danilovfa.presentation.patient.create.store.PatientCreateStoreFactory

internal class DefaultPatientCreateComponent(
    patient: Patient?,
    storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : PatientCreateComponent, StatefulDefaultComponent<Intent, State, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        PatientCreateStoreFactory(storeFactory).create(patient = patient)
    }

    override val stateFlow = store.stateFlow

    init {
        backHandler.onBackClicked { onIntent(Intent.OnBackClicked) }
        observeLabels(store.labels) { label ->
            when (label) {
                Label.NavigateBack -> output(Output.NavigateBack)
                is Label.NavigatePatient -> output(Output.NavigatePatient(label.patientId))
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)
}