package com.danilovfa.common.uikit.composables.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.uikit.composables.animation.IconAnimatedVisibility
import com.danilovfa.common.uikit.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun ScrollToTopButton(
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    IconAnimatedVisibility(
        visible = state.isScrollingUp().not(),
        modifier = modifier
    ) {
        FloatingActionButton(
            containerColor = AppTheme.colors.primaryDisabled,
            shape = CircleShape,
            onClick = {
                scope.launch {
                    state.animateScrollToItem(0)
                }
            }
        ) {
            Icon(
                painter = AppIcon.ArrowUp,
                tint = AppTheme.colors.onPrimary,
                contentDescription = "Scroll up"
            )
        }
    }
}