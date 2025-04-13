package com.danilovfa.presentation.analysis

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.danilovfa.common.base.component.stateful.StatefulDefaultComponent
import com.danilovfa.common.base.dialog.AlertDialogState
import com.danilovfa.common.base.utils.decompose.onBackClicked
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.analysis.AnalyzeComponent.Output
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Label
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State
import com.danilovfa.presentation.analysis.store.AnalyzeStoreFactory
import com.danilovfa.common.resources.strings

class DefaultAnalyzeComponent(
    private val recordingId: Long,
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : AnalyzeComponent, StatefulDefaultComponent<Intent, State, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        AnalyzeStoreFactory(storeFactory).create(recordingId = recordingId)
    }

    override val stateFlow = store.stateFlow

    init {
        backHandler.onBackClicked { onIntent(Intent.OnBackClicked) }
        observeLabels(store.labels) { label ->
            when (label) {
                Label.NavigateBack -> output(Output.NavigateBack)
                is Label.ShowError -> showAnalyzeError(label.text)
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)

    private fun showAnalyzeError(text: Text?) {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.error),
            text = text ?: Text.Resource(strings.something_went_wrong),
            confirmButtonTitle = Text.Resource(strings.refresh),
            dismissButtonTitle = Text.Resource(strings.analyze_error_navigate_back),
            onConfirmClick = {
                onIntent(Intent.RetryAnalyze)
                alertDialogDelegate.dismissAlertDialog()
            },
            onDismissClick = {
                onIntent(Intent.OnBackClicked)
                alertDialogDelegate.dismissAlertDialog()
            }
        )

        alertDialogDelegate.updateAlertDialogState(alertDialogState)
    }
}