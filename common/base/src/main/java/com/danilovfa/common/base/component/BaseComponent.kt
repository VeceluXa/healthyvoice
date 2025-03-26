package com.danilovfa.common.base.component

import com.danilovfa.common.base.dialog.AlertDialogDelegate
import com.danilovfa.common.base.event.EventDelegate
import com.danilovfa.common.base.permission.RequestPermissionEventDelegate

interface BaseComponent {
    val eventDelegate: EventDelegate
    val requestPermissionEventDelegate: RequestPermissionEventDelegate
    val alertDialogDelegate: AlertDialogDelegate
}