package com.danilovfa.common.base.permission

import com.danilovfa.common.base.event.Event

class RequestPermissionEvent(val permission: String) : Event

sealed class PermissionStatus {

    abstract val permission: String

    data class Granted(override val permission: String) : PermissionStatus()
    data class Denied(override val permission: String) : PermissionStatus()
    data class NeedsRationale(override val permission: String) : PermissionStatus()
}