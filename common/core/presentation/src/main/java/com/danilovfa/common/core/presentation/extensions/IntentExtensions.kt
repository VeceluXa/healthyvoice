package com.danilovfa.common.core.presentation.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ActivityResultLauncher<Intent>.LaunchAppSettings() {
    val packageName = LocalContext.current.packageName
    launchAppSettings(packageName)
}

fun ActivityResultLauncher<Intent>.launchAppSettings(context: Context) {
    launchAppSettings(context.packageName)
}

private fun ActivityResultLauncher<Intent>.launchAppSettings(packageName: String) {
    val intent = Intent(
        ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    )
    launch(intent)
}