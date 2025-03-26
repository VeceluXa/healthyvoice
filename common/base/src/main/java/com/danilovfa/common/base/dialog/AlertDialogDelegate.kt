package com.danilovfa.common.base.dialog

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlertDialogDelegate {

    private val _alertDialogStateFlow: MutableStateFlow<AlertDialogState?> by lazy { MutableStateFlow(null) }
    val alertDialogStateFlow: StateFlow<AlertDialogState?> = _alertDialogStateFlow.asStateFlow()
    val alertDialogState: AlertDialogState? get() = alertDialogStateFlow.value

    fun updateAlertDialogState(state: AlertDialogState?) {
        _alertDialogStateFlow.value = state
    }
    fun dismissAlertDialog() {
        _alertDialogStateFlow.value = null
    }
}