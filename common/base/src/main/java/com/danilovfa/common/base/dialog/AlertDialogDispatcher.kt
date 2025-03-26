package com.danilovfa.common.base.dialog

interface AlertDialogDispatcher {
    val alertDialogDelegate: AlertDialogDelegate

    fun updateAlertDialogState(state: AlertDialogState) {
        alertDialogDelegate.updateAlertDialogState(state)
    }

    fun dismissDialog() {
        alertDialogDelegate.dismissAlertDialog()
    }
}