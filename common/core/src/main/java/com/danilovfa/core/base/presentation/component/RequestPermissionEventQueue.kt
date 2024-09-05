package com.danilovfa.core.base.presentation.component

import kotlinx.coroutines.flow.StateFlow
import com.danilovfa.core.base.presentation.event.RequestPermissionEvent

interface RequestPermissionEventQueue {

    val requestPermissionFlow: StateFlow<RequestPermissionEvent?>

    fun requestPermission(permission: String)

    fun dismissRequestPermission()
}