package com.danilovfa.feature.record

import android.Manifest
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.core.base.presentation.component.StatefulDefaultComponent
import com.danilovfa.core.base.presentation.event.PermissionStatus
import com.danilovfa.core.library.decompose.onBackClicked
import com.danilovfa.core.library.dialog.AlertDialogState
import com.danilovfa.core.library.text.Text
import com.danilovfa.feature.record.RecordComponent.Output
import com.danilovfa.feature.record.store.RecordStore
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State
import com.danilovfa.feature.record.store.RecordStoreFactory
import com.danilovfa.resources.drawable.strings

class DefaultRecordComponent(
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : RecordComponent, StatefulDefaultComponent<State, Intent, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        RecordStoreFactory(storeFactory).create()
    }

    override val stateFlow = store.stateFlow

    init {
        observeLabels(store.labels) { label ->
            when (label) {
                is Label.Analyze -> output(Output.Analyze(label.filename))
                Label.RequestAudioPermission -> requestPermission(Manifest.permission.RECORD_AUDIO)
                Label.ShowRationale -> showAudioPermissionRationale()
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
                offerEvent(RecordComponent.Events.OpenAppSettings)
                dismissAlertDialog()
            },
            onDismissClick = ::dismissAlertDialog
        )
        updateAlertDialogState(alertDialogState)
    }
}