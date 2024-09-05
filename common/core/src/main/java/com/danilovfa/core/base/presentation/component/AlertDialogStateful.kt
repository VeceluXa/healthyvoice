package com.danilovfa.core.base.presentation.component

import kotlinx.coroutines.flow.StateFlow
import com.danilovfa.core.library.dialog.AlertDialogState

interface AlertDialogStateful {

    val alertDialogStateFlow: StateFlow<AlertDialogState?>
    val alertDialogState: AlertDialogState?

    fun updateAlertDialogState(state: AlertDialogState?)
    fun dismissAlertDialog()
}