package com.danilovfa.common.uikit.modifier

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize

/** Remembers minimal size if the given [predicate] returns `true`. */
fun Modifier.rememberMinSize(predicate: (old: IntSize, new: IntSize) -> Boolean): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "rememberMinSize"
    }
) {
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    this
        .onGloballyPositioned { coordinates ->
            val size = coordinates.size
            if (predicate(contentSize, size)) contentSize = size
        }
        .sizeIn(
            minHeight = with(density) { contentSize.height.toDp() },
            minWidth = with(density) { contentSize.width.toDp() },
        )
}

fun Modifier.fillWidthOfParent(parentPadding: Dp) = this.then(
    layout { measurable, constraints ->
        // This is to force layout to go beyond the borders of its parent
        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth + 2 * parentPadding.roundToPx(),
            ),
        )
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    },
)