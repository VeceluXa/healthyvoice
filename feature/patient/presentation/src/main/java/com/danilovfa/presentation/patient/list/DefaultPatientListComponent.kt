package com.danilovfa.presentation.patient.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.common.base.component.stateful.StatefulDefaultComponent
import com.danilovfa.common.uikit.event.showTodo
import com.danilovfa.presentation.patient.list.PatientListComponent.Output
import com.danilovfa.presentation.patient.list.store.PatientListStore.Intent
import com.danilovfa.presentation.patient.list.store.PatientListStore.Label
import com.danilovfa.presentation.patient.list.store.PatientListStore.State
import com.danilovfa.presentation.patient.list.store.PatientListStoreFactory

internal class DefaultPatientListComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : PatientListComponent, StatefulDefaultComponent<Intent, State, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        PatientListStoreFactory(storeFactory).create()
    }

    override val stateFlow = store.stateFlow

    init {
        observeLabels(store.labels) { label ->
            when (label) {
                Label.CreatePatient -> output(Output.NavigatePatientCreate)
                Label.ExportPatients -> eventDelegate.showTodo()
                is Label.ShowPatientDetails -> output(Output.NavigatePatientDetails(label.patientId))
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)
}