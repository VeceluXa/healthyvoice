package com.danilovfa.presentation.record.main

import android.Manifest
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.common.base.component.stateful.StatefulDefaultComponent
import com.danilovfa.common.base.dialog.AlertDialogState
import com.danilovfa.common.base.utils.decompose.onBackClicked
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.presentation.record.main.RecordComponent.Output
import com.danilovfa.presentation.record.main.store.RecordStore.Intent
import com.danilovfa.presentation.record.main.store.RecordStore.Label
import com.danilovfa.presentation.record.main.store.RecordStore.State
import com.danilovfa.presentation.record.main.store.RecordStoreFactory
import com.danilovfa.common.resources.drawable.AppIllustration
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.event.showError

internal class DefaultRecordComponent(
    private val patientId: Long,
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : RecordComponent, StatefulDefaultComponent<Intent, State, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        RecordStoreFactory(storeFactory).create(patientId = patientId)
    }

    override val stateFlow = store.stateFlow

    init {
        backHandler.onBackClicked { onIntent(Intent.OnBackClicked) }
        observeLabels(store.labels) { label ->
            when (label) {
                Label.NavigateBack -> output(Output.NavigateBack)
                is Label.Analyze -> output(Output.NavigateCut(label.recordingId))
                Label.RequestAudioPermission -> requestPermissionEventDelegate.requestPermission(Manifest.permission.RECORD_AUDIO)
                Label.ShowRationale -> showAudioPermissionRationale()
                Label.ShowHelpDialog -> showHelpDialog()
                is Label.ShowError -> eventDelegate.showError(label.text)
                Label.OpenFilePicker -> eventDelegate.offerEvent(RecordComponent.RecordEvent.OpenFilePicker)
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)

    private fun showAudioPermissionRationale() {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.record_permission_rationale_title),
            text = Text.Resource(strings.record_permission_rationale_description),
            confirmButtonTitle = Text.Resource(strings.settings),
            dismissButtonTitle = Text.Resource(strings.dismiss),
            onConfirmClick = {
                eventDelegate.offerEvent(RecordComponent.RecordEvent.OpenAppSettings)
                alertDialogDelegate.dismissAlertDialog()
            },
            onDismissClick = alertDialogDelegate::dismissAlertDialog
        )
        alertDialogDelegate.updateAlertDialogState(alertDialogState)
    }

    private fun showHelpDialog() {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.record_help_dialog_title),
            text = Text.Resource(strings.record_help_dialog_description),
            illustration = { AppIllustration.RecordHelpDialog },
            dismissButtonTitle = Text.Resource(strings.its_clear),
            onDismissClick = alertDialogDelegate::dismissAlertDialog
        )
        alertDialogDelegate.updateAlertDialogState(alertDialogState)
    }
}