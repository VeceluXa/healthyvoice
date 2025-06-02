package com.danilovfa.export.presentation.utils

import android.app.ActivityManager
import android.content.Context

fun isAppInForeground(context: Context): Boolean {
    val appProcessInfo = ActivityManager.RunningAppProcessInfo()
    ActivityManager.getMyMemoryState(appProcessInfo)
    return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}