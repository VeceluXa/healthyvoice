package com.danilovfa.feature.analyze

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
import com.danilovfa.feature.analyze.AnalyzeComponent.Output
import com.danilovfa.feature.analyze.store.AnalyzeStore.Intent
import com.danilovfa.feature.analyze.store.AnalyzeStore.Label
import com.danilovfa.feature.analyze.store.AnalyzeStore.State
import com.danilovfa.feature.analyze.store.AnalyzeStoreFactory
import com.danilovfa.resources.drawable.strings

class DefaultAnalyzeComponent(
    private val audioData: AudioData,
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : AnalyzeComponent, StatefulDefaultComponent<State, Intent, Label>(componentContext) {
    private val store = instanceKeeper.getStore {
        AnalyzeStoreFactory(storeFactory).create(audioData)
    }

    override val stateFlow = store.stateFlow

    init {
        backHandler.onBackClicked { onIntent(Intent.OnBackClicked) }
        observeLabels(store.labels) { label ->
            when (label) {
                Label.NavigateBack -> output(Output.NavigateBack)
                Label.ShowConfirmNavigateBack -> showConfirmNavigateBack()
                is Label.ShowError -> showAnalyzeError(label.text)
            }
        }
    }

    override fun onIntent(intent: Intent) = store.accept(intent)

    private fun showConfirmNavigateBack() {
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

    private fun showAnalyzeError(text: Text?) {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = Text.Resource(strings.error),
            text = text ?: Text.Resource(strings.something_went_wrong),
            confirmButtonTitle = Text.Resource(strings.refresh),
            dismissButtonTitle = Text.Resource(strings.analyze_error_navigate_back),
            onConfirmClick = {
                onIntent(Intent.RetryAnalyze)
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