package com.danilovfa.common.base.permission

import com.danilovfa.common.base.permission.RequestPermissionEventDelegate

interface RequestPermissionEventDispatcher {
    val requestPermissionEventDelegate: RequestPermissionEventDelegate

    fun requestPermission(permission: String) {
        requestPermissionEventDelegate.requestPermission(permission)
    }

    fun dismissRequestPermission() {
        requestPermissionEventDelegate.dismissRequestPermission()
    }
}