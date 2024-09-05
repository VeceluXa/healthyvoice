package com.danilovfa.uikit.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.tiny

@Composable
fun Modifier.surface(
    backgroundColor: Color = AppTheme.colors.surface,
    shape: Shape = MaterialTheme.shapes.tiny,
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
): Modifier {
    return this
        .shadow(elevation, shape)
        .background(color = backgroundColor, shape = shape)
        .clip(shape)
        .optional(onClick) { clickable(onClick = it) }
}