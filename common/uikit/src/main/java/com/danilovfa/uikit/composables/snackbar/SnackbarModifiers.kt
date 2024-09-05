package com.danilovfa.uikit.composables.snackbar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.snackbarPadding(state: SnackbarHostState): Modifier {
    return this.padding(top = state.paddingTop, bottom = state.paddingBottom)
}

fun Modifier.animatedSnackbarPadding(state: SnackbarHostState): Modifier = composed {
    val topPad by animateDpAsState(state.paddingTop, label = "SnackbarTopPadding")
    val bottomPad by animateDpAsState(state.paddingBottom, label = "SnackbarBottomPadding")
    this.padding(top = topPad, bottom = bottomPad)
}
