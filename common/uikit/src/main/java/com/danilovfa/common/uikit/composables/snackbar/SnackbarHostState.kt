package com.danilovfa.common.uikit.composables.snackbar

import android.graphics.Bitmap
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import androidx.compose.material3.Snackbar as Material3Snackbar
import androidx.compose.material3.SnackbarHostState as Material3SnackbarHostState

@Composable
fun rememberSnackbarHostState(): SnackbarHostState = remember {
    SnackbarHostState(Material3SnackbarHostState())
}

@Stable
class SnackbarHostState(val delegate: Material3SnackbarHostState) {
    var snackbarAlignment: SnackbarAlignment by mutableStateOf(SnackbarAlignment.BOTTOM)

    var paddingTop: Dp by mutableStateOf(0.dp)
    var paddingBottom: Dp by mutableStateOf(0.dp)

    private val mutex by lazy { getField<Mutex>() }
    private val snackbarData by lazy { getField<MutableState<SnackbarData?>>() }
    private var currentContinuation: CancellableContinuation<*>? = null

    fun resetPadding() {
        paddingBottom = 0.dp
        paddingTop = 0.dp
    }

    suspend fun showSnackbar(
        message: String,
        image: Bitmap? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarStyle: SnackbarStyle = SnackbarStyle.Default
    ): SnackbarResult {
        currentContinuation?.cancel()
        return mutex.withLock {
            val snackbarDataState = snackbarData
            try {
                suspendCancellableCoroutine { continuation ->
                    currentContinuation = continuation.takeUnless { snackbarStyle == SnackbarStyle.Error }
                    snackbarDataState.value = StyledSnackbarData(
                        message = message,
                        image = image,
                        actionLabel = null,
                        duration = duration,
                        style = snackbarStyle,
                        continuation = continuation
                    )
                }
            } finally {
                snackbarAlignment = SnackbarAlignment.BOTTOM
                snackbarDataState.value = null
                currentContinuation = null
            }
        }
    }

    enum class SnackbarAlignment {
        TOP, BOTTOM;

        val isBottom: Boolean
            get() = this == BOTTOM
    }

    @Suppress("ExplicitCollectionElementAccessMethod")
    private inline fun <reified T> getField(): T {
        return Material3SnackbarHostState::class.java.declaredFields
            .first { T::class.java.isAssignableFrom(it.type) }
            .apply { isAccessible = true }
            .get(delegate) as T
    }
}

@Suppress("ForbiddenMethodCall")
@Composable
fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.SnackbarHost(
        hostState = hostState.delegate,
        modifier = modifier,
        snackbar = { StyledSnackbar(it) },
    )
}

@Composable
private fun StyledSnackbar(snackbarData: SnackbarData) {
    if (snackbarData is StyledSnackbarData) {
        Snackbar(snackbarData = snackbarData)
    } else {
        Material3Snackbar(snackbarData)
    }
}

@Composable
fun SnackbarPaddingEffect(
    bottom: Dp = 0.dp,
    top: Dp = 0.dp,
    state: SnackbarHostState = LocalSnackbarHostState.current
) {
    DisposableEffect(bottom, top) {
        state.paddingBottom = bottom
        state.paddingTop = top
        onDispose {
            state.resetPadding()
        }
    }
}

@Composable
internal fun rememberSnackbarHostState(snackbarHostState: Material3SnackbarHostState): SnackbarHostState {
    return remember(snackbarHostState) { SnackbarHostState(snackbarHostState) }
}
