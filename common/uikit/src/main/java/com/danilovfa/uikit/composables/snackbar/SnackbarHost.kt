package com.danilovfa.uikit.composables.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.danilovfa.uikit.composables.snackbar.SnackbarHostState.SnackbarAlignment

@Composable
fun SnackbarHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier.fillMaxSize()) {
        val snackbarHostState = rememberSnackbarHostState()

        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Scaffold(
                snackbarHost = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = if (snackbarHostState.snackbarAlignment == SnackbarAlignment.BOTTOM) {
                            Alignment.BottomCenter
                        } else {
                            Alignment.TopCenter
                        }
                    ) {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .snackbarPadding(snackbarHostState)
                                .navigationBarsPadding()
                                .imePadding()
                        )
                    }
                }
            ) { paddingValues ->
                // Use required padding values
                @Suppress("UNUSED_EXPRESSION")
                paddingValues

                content()
            }
        }
    }
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState not provided")
}
