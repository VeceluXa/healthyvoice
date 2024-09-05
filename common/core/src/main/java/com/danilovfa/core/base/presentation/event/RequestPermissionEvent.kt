package com.danilovfa.core.base.presentation.event

class RequestPermissionEvent(val permission: String) : Event

sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data object Denied : PermissionStatus()
    data object NeedsRationale : PermissionStatus()
}