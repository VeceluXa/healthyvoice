package com.danilovfa.core.library.context

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber

fun Context.sendEmail(to: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.fromParts("mailto", to, null)
            putExtra(Intent.EXTRA_EMAIL, to)
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.e(e)
    } catch (t: Throwable) {
        Timber.e(t)
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
        Timber.e(e)
    } catch (t: Throwable) {
        Timber.e(t)
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
        Timber.e(e)
    } catch (t: Throwable) {
        Timber.e(t)
    }
}