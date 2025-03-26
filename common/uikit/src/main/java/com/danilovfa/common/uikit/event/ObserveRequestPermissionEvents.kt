package com.danilovfa.common.uikit.event

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.danilovfa.common.base.permission.PermissionStatus
import com.danilovfa.common.base.permission.RequestPermissionEvent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("IgnoredReturnValue")
@Composable
fun ObserveRequestPermissionEvents(
    requestPermissionEvents: StateFlow<RequestPermissionEvent?>,
    lifecycleOwner: LifecycleOwner? = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onPermissionResult: (PermissionStatus) -> Unit = {},
) {
    val activity = LocalActivity.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val permission = requestPermissionEvents.value?.permission

        if (permission != null && activity != null) {
            when {
                isGranted -> onPermissionResult(PermissionStatus.Granted(permission))

                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                    onPermissionResult(PermissionStatus.NeedsRationale(permission))
                }

                else -> onPermissionResult(PermissionStatus.Denied(permission))
            }
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionEvents
            .apply {
                if (lifecycleOwner != null) flowWithLifecycle(
                    lifecycleOwner.lifecycle,
                    minActiveState
                )
            }
            .onEach { event -> event?.let { permissionLauncher.launch(it.permission) } }
            .launchIn(this)
    }
}