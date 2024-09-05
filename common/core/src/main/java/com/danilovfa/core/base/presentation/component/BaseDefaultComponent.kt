package com.danilovfa.core.base.presentation.component

import com.danilovfa.resources.R
import com.danilovfa.core.base.presentation.event.EventQueue
import com.danilovfa.core.base.presentation.event.EventsDispatcher
import com.danilovfa.core.base.presentation.event.RequestPermissionEvent
import com.danilovfa.core.library.decompose.coroutineScope
import com.danilovfa.core.library.dialog.AlertDialogState
import com.danilovfa.core.library.exception.getErrorDialogDescription
import com.danilovfa.core.library.exception.getErrorDialogTitle
import com.danilovfa.core.library.text.Text
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface BaseComponent : EventsDispatcher, AlertDialogStateful, RequestPermissionEventQueue

abstract class BaseDefaultComponent(private val componentContext: ComponentContext) :
    ComponentContext by componentContext,
    BaseComponent {
    protected val scope = coroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val events: EventQueue = EventQueue()

    private val _alertDialogStateFlow by lazy<MutableStateFlow<AlertDialogState?>> {
        MutableStateFlow(null)
    }
    final override val alertDialogStateFlow: StateFlow<AlertDialogState?>
        get() = _alertDialogStateFlow.asStateFlow()
    final override val alertDialogState: AlertDialogState? get() = alertDialogStateFlow.value

    private val _requestPermissionStateFlow by lazy<MutableStateFlow<RequestPermissionEvent?>> {
        MutableStateFlow(null)
    }
    override val requestPermissionFlow: StateFlow<RequestPermissionEvent?>
        get() = _requestPermissionStateFlow.asStateFlow()

    final override fun updateAlertDialogState(state: AlertDialogState?) {
        _alertDialogStateFlow.value = state
    }

    final override fun dismissAlertDialog() {
        updateAlertDialogState(null)
    }

    override fun requestPermission(permission: String) {
        _requestPermissionStateFlow.value = RequestPermissionEvent(permission)
    }

    override fun dismissRequestPermission() {
        _requestPermissionStateFlow.value = null
    }

    protected fun showAlertDialog(
        alertDialogState: AlertDialogState
    ) {
        updateAlertDialogState(alertDialogState)
    }

    protected fun showErrorAlertDialog(
        error: Throwable,
        onRetryClick: () -> Unit,
        onDismissClick: () -> Unit = ::dismissAlertDialog
    ) {
        val alertDialogState = AlertDialogState.DefaultDialogState(
            title = error.getErrorDialogTitle(),
            text = error.getErrorDialogDescription(),
            onConfirmClick = onRetryClick,
            onDismissClick = onDismissClick,
            confirmButtonTitle = Text.Resource(R.string.refresh),
            dismissButtonTitle = Text.Resource(R.string.dismiss)
        )
        updateAlertDialogState(alertDialogState)
    }
}

interface StatefulComponent<State : Any, Intent : Any> : BaseComponent, Stateful<State, Intent>

abstract class StatefulDefaultComponent<State : Any, Intent : Any, Label : Any>(
    componentContext: ComponentContext,
) : BaseDefaultComponent(componentContext), Stateful<State, Intent> {

    protected fun observeLabels(labels: Flow<Label>, onLabel: (label: Label) -> Unit) {
        labels
            .onEach { onLabel(it) }
            .launchIn(scope)
    }
}
