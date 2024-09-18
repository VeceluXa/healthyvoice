package com.danilovfa.feature.cut

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.core.base.presentation.component.StatefulDefaultComponent
import com.danilovfa.core.library.decompose.onBackClicked
import com.danilovfa.core.library.dialog.AlertDialogState
import com.danilovfa.core.library.text.Text
import com.danilovfa.data.common.model.AudioData
import com.danilovfa.feature.cut.CutComponent.Output
import com.danilovfa.feature.cut.store.CutStore.Intent
import com.danilovfa.feature.cut.store.CutStore.Label
import com.danilovfa.feature.cut.store.CutStore.State
import com.danilovfa.feature.cut.store.CutStoreFactory
import com.danilovfa.resources.drawable.strings

class DefaultCutComponent(
    private val audioData: AudioData,
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : CutComponent, StatefulDefaultComponent<State, Intent, Label>(componentContext) {
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
                dismissAlertDialog()
            },
            onDismissClick = ::dismissAlertDialog
        )

        updateAlertDialogState(alertDialogState)
    }

    private fun showLoadErrorDialog() {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.error),
            text = Text.Resource(strings.cut_load_error),
            confirmButtonTitle = Text.Resource(strings.try_again),
            dismissButtonTitle = Text.Resource(strings.analyze_error_navigate_back),
            onConfirmClick = {
                onIntent(Intent.RetryLoadRecording)
                dismissAlertDialog()
            },
            onDismissClick = {
                onIntent(Intent.OnBackConfirmed)
                dismissAlertDialog()
            }
        )

        updateAlertDialogState(alertDialogState)
    }
}