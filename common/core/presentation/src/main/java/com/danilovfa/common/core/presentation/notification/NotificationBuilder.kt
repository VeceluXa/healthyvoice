package com.danilovfa.core.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat

data class NotificationChannelData(
    val id: String,
    @StringRes val descriptionRes: Int,
    val group: String? = null,
    val importance: Int = NotificationManager.IMPORTANCE_HIGH
)

fun Context.getNotificationBuilder(
    channel: NotificationChannelData
): NotificationCompat.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channel.id,
            getString(channel.descriptionRes),
            channel.importance
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(notificationChannel)
        NotificationCompat.Builder(this, notificationChannel.id)
    } else {
        @Suppress("DEPRECATION")
        NotificationCompat.Builder(this)
    }
}