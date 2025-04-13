package com.danilovfa.presentation.patient.detail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.common.base.component.stateful.StatefulDefaultComponent
import com.danilovfa.common.base.dialog.AlertDialogState
import com.danilovfa.common.base.utils.decompose.onBackClicked
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.event.showTodo
import com.danilovfa.presentation.patient.detail.PatientDetailComponent.Output
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Intent
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Label
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.State
import com.danilovfa.presentation.patient.detail.store.PatientDetailsStoreFactory

internal class DefaultPatientDetailComponent(
    private val patientId: Long,
    storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : PatientDetailComponent, StatefulDefaultComponent<Intent, State, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        PatientDetailsStoreFactory(storeFactory).create(patientId = patientId)
    }

    override val stateFlow = store.stateFlow

    init {
        backHandler.onBackClicked { onIntent(Intent.OnBackClicked) }
        observeLabels(store.labels) { label ->
            when (label) {
                is Label.EditPatient -> output(Output.NavigateEdit(label.patient))
                Label.ExportPatient -> eventDelegate.showTodo()
                Label.NavigateBack -> output(Output.NavigateBack)
                Label.NewRecord -> output(Output.NavigateRecord(patientId))
                is Label.ShowAnalysis -> output(Output.NavigateAnalysis(label.recordingId))
                Label.ShowConfirmDeleteDialog -> showConfirmDeleteDialog()
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)

    private fun showConfirmDeleteDialog() {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.patient_delete_dialog_title),
            text = Text.Resource(strings.patient_delete_dialog_description),
            confirmButtonTitle = Text.Resource(strings.delete),
            dismissButtonTitle = Text.Resource(strings.dismiss),
            isConfirmNegative = true,
            onConfirmClick = {
                onIntent(Intent.ConfirmDeletePatient)
                alertDialogDelegate.dismissAlertDialog()
            },
            onDismissClick = alertDialogDelegate::dismissAlertDialog
        )

        alertDialogDelegate.updateAlertDialogState(alertDialogState)
    }
}