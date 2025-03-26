package com.danilovfa.core.presentation.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat

@SuppressLint("MissingPermission")
fun Notification.show(
    id: Int,
    context: Context
) {
    with(NotificationManagerCompat.from(context)) {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
        }

        notify(id, this@show)
    }
}