package com.danilovfa.common.uikit.event

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.danilovfa.common.base.event.Event
import com.danilovfa.common.base.event.EventDelegate
import com.danilovfa.common.core.presentation.get
import com.danilovfa.common.uikit.composables.snackbar.LocalSnackbarHostState
import com.danilovfa.common.uikit.composables.snackbar.SnackbarHostState
import com.danilovfa.common.uikit.composables.snackbar.SnackbarStyle
import com.danilovfa.common.uikit.event.ErrorMessageEvent
import com.danilovfa.common.uikit.event.MessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun ObserveEvents(
    events: EventDelegate,
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current,
    resources: Resources = LocalContext.current.resources,
    lifecycleOwner: LifecycleOwner? = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEvent: (Event) -> Boolean = { false },
) {
    ObserveEventsDefault(
        events = events,
        lifecycleOwner = lifecycleOwner,
        minActiveState = minActiveState,
        onEvent = { event ->
            tryHandleCommonEvent(
                event = event,
                snackbarHostState = snackbarHostState,
                resources = resources,
            ) || onEvent.invoke(event)
        }
    )
}

@Suppress("ComposableParametersOrdering", "IgnoredReturnValue")
@Composable
private fun ObserveEventsDefault(
    events: EventDelegate,
    lifecycleOwner: LifecycleOwner? = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEvent: CoroutineScope.(Event) -> Unit,
) {
    LaunchedEffect(Unit) {
        events.flow
            .apply { if (lifecycleOwner != null) flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState) }
            .onEach { onEvent(it) }
            .launchIn(this)
    }
}

/**
 * Handles the given [event] if it is default message or navigation event.
 * @return `true` if the given event handled, otherwise `false`.
 * @see tryHandleMessageEvent
 */
internal fun CoroutineScope.tryHandleCommonEvent(
    event: Event,
    snackbarHostState: SnackbarHostState,
    resources: Resources,
): Boolean {
    return tryHandleMessageEvent(event, snackbarHostState, resources)
}

/**
 * Handles the given [event] if it is a message event.
 * Uses [snackbarHostState] to show messages in snackbar.
 * @return `true` if the given event handled, otherwise `false`.
 * @see tryHandleCommonEvent
 */
internal fun CoroutineScope.tryHandleMessageEvent(
    event: Event,
    snackbarHostState: SnackbarHostState,
    resources: Resources,
): Boolean {
    when (event) {
        is MessageEvent -> launch {
            snackbarHostState.showSnackbar(
                message = event.message.get(resources),
                snackbarStyle = event.snackbarStyle,
            )
        }
        is ErrorMessageEvent -> launch {
            snackbarHostState.showSnackbar(
                message = event.message.get(resources),
                snackbarStyle = SnackbarStyle.Error,
            )
        }
        else -> return false
    }
    return true
}
