package com.danilovfa.uikit.composables.event

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.danilovfa.core.base.presentation.event.Event
import com.danilovfa.core.base.presentation.event.EventQueue
import com.danilovfa.core.library.text.get
import com.danilovfa.uikit.composables.snackbar.LocalSnackbarHostState
import com.danilovfa.uikit.composables.snackbar.SnackbarHostState
import com.danilovfa.uikit.composables.snackbar.SnackbarStyle

@Composable
fun ObserveEvents(
    events: EventQueue,
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
    events: EventQueue,
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
        is ImageMessageEvent -> launch {
            snackbarHostState.showSnackbar(
                message = event.message.get(resources),
                image = event.image,
                snackbarStyle = SnackbarStyle.Image
            )
        }
        else -> return false
    }
    return true
}
