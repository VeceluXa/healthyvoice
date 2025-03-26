package com.danilovfa.common.core.presentation.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger

private const val TAG = "ComposeBroadcastReceiver"

@Composable
fun SystemBroadcastReceiver(
    action: String,
    isExported: Boolean = true,
    onSystemEvent: (intent: Intent?) -> Unit
) {
    SystemBroadcastReceiver(
        actions = listOf(action),
        isExported = isExported,
        onSystemEvent = onSystemEvent
    )
}

@Composable
fun SystemBroadcastReceiver(
    actions: List<String>,
    isExported: Boolean = true,
    onSystemEvent: (intent: Intent?) -> Unit
) {
    val context = LocalContext.current

    val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)

    DisposableEffect(context, actions) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Logger.withTag(TAG).d(intent.toString())
                currentOnSystemEvent(intent)
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter().apply {
                actions.forEach { addAction(it) }
            },
            if (isExported) ContextCompat.RECEIVER_EXPORTED else ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}