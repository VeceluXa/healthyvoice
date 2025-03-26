package com.danilovfa.presentation.cut

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.common.base.component.stateful.StatefulDefaultComponent
import com.danilovfa.common.base.dialog.AlertDialogState
import com.danilovfa.common.base.utils.decompose.onBackClicked
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.presentation.cut.CutComponent.Output
import com.danilovfa.presentation.cut.store.CutStore.Intent
import com.danilovfa.presentation.cut.store.CutStore.Label
import com.danilovfa.presentation.cut.store.CutStore.State
import com.danilovfa.presentation.cut.store.CutStoreFactory
import com.danilovfa.common.resources.strings
import com.danilovfa.domain.record.repository.model.AudioData

class DefaultCutComponent(
    private val audioData: AudioData,
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : CutComponent, StatefulDefaultComponent<Intent, State, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        CutStoreFactory(storeFactory).create(audioData)
    }

    override val stateFlow = store.stateFlow

    init {
        backHandler.onBackClicked { onIntent(Intent.OnBackClicked) }
        observeLabels(store.labels) { label ->
            when (label) {
                is Label.NavigateAnalyze -> output(Output.Analyze(label.data))
                Label.NavigateBack -> output(Output.NavigateBack)
                Label.ShowBackConfirmationDialog -> showBackConfirmationDialog()
                Label.ShowLoadRecordingError -> showLoadErrorDialog()
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)

    private fun showBackConfirmationDialog() {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.analyze_back_confirm_title),
            text = Text.Resource(strings.analyze_back_confirm_text),
            confirmButtonTitle = Text.Resource(strings.confirm),
            dismissButtonTitle = Text.Resource(strings.dismiss),
            onConfirmClick = {
                onIntent(Intent.OnBackConfirmed)
                alertDialogDelegate.dismissAlertDialog()
            },
            onDismissClick = alertDialogDelegate::dismissAlertDialog
        )

        alertDialogDelegate.updateAlertDialogState(alertDialogState)
    }

    private fun showLoadErrorDialog() {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.error),
            text = Text.Resource(strings.cut_load_error),
            confirmButtonTitle = Text.Resource(strings.try_again),
            dismissButtonTitle = Text.Resource(strings.analyze_error_navigate_back),
            onConfirmClick = {
                onIntent(Intent.RetryLoadRecording)
                alertDialogDelegate.dismissAlertDialog()
            },
            onDismissClick = {
                onIntent(Intent.OnBackConfirmed)
                alertDialogDelegate.dismissAlertDialog()
            }
        )

        alertDialogDelegate.updateAlertDialogState(alertDialogState)
    }
}