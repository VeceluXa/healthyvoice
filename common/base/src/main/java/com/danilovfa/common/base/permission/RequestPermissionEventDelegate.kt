package com.danilovfa.common.base.permission

import com.danilovfa.common.base.permission.RequestPermissionEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RequestPermissionEventDelegate {

    private val _requestPermissionStateFlow by lazy<MutableStateFlow<RequestPermissionEvent?>> {
        MutableStateFlow(null)
    }
    val requestPermissionFlow: StateFlow<RequestPermissionEvent?>
        get() = _requestPermissionStateFlow.asStateFlow()


    fun requestPermission(permission: String) {
        _requestPermissionStateFlow.value = RequestPermissionEvent(permission)
    }

    fun dismissRequestPermission() {
        _requestPermissionStateFlow.value = null
    }
}