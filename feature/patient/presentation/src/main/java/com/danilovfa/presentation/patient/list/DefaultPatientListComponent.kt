package com.danilovfa.presentation.patient.list

import android.Manifest
import android.os.Build
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.common.base.component.stateful.StatefulDefaultComponent
import com.danilovfa.common.base.dialog.AlertDialogState
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.event.showTodo
import com.danilovfa.presentation.patient.list.PatientListComponent.Output
import com.danilovfa.presentation.patient.list.PatientListComponent.PatientListEvents
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
                is Label.ShowPatientDetails -> output(Output.NavigatePatientDetails(label.patientId))
                Label.DismissRequestNotificationPermission -> {
                    requestPermissionEventDelegate.dismissRequestPermission()
                    alertDialogDelegate.dismissAlertDialog()
                }
                Label.RequestNotificationPermission -> requestNotificationsPermission()
                Label.ShowNotificationPermissionRationale -> showNotificationPermissionRationale()
                Label.ShowTodo -> eventDelegate.showTodo()
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionEventDelegate.requestPermission(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showNotificationPermissionRationale() {
        val dialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.dialog_notifications_rationale_title),
            text = Text.Resource(strings.dialog_notifications_rationale_text),
            confirmButtonTitle = Text.Resource(strings.dialog_permissions_open_settings),
            onConfirmClick = { eventDelegate.offerEvent(PatientListEvents.LaunchAppSettings) },
            onDismissClick = {  }
        )

        alertDialogDelegate.updateAlertDialogState(dialogState)
    }
}