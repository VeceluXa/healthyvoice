package com.danilovfa.common.base.component

import com.danilovfa.common.base.dialog.AlertDialogDelegate
import com.danilovfa.common.base.event.EventDelegate
import com.danilovfa.common.base.permission.RequestPermissionEventDelegate
import com.danilovfa.common.base.utils.decompose.coroutineScope
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseDefaultComponent(private val componentContext: ComponentContext) : BaseComponent,
    ComponentContext by componentContext {
    protected val scope = coroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val alertDialogDelegate = AlertDialogDelegate()
    override val eventDelegate: EventDelegate = EventDelegate()
    override val requestPermissionEventDelegate: RequestPermissionEventDelegate =
        RequestPermissionEventDelegate()
}