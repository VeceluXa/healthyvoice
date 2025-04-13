package com.danilovfa.common.core.presentation.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import co.touchlab.kermit.Logger

fun Context.sendEmail(to: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.fromParts("mailto", to, null)
            putExtra(Intent.EXTRA_EMAIL, to)
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Logger.e("Context.sendEmail", e)
    } catch (t: Throwable) {
        Logger.e("Context.sendEmail", t)
    }
}

fun Context.openLink(link: String) {
    try {
        val parsedLink = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = parsedLink
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Logger.e("Context.openLink", e)
    } catch (t: Throwable) {
        Logger.e("Context.openLink", t)
    }
}

fun Context.shareLink(link: String) {
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Logger.e("Context.openLink", e)
    } catch (t: Throwable) {
        Logger.e("Context.openLink", t)
    }
}

fun Context.getPackageInfo(): PackageInfo {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)
    }
}