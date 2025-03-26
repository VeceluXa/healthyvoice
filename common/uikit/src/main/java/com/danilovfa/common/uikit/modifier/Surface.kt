package com.danilovfa.common.uikit.modifier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.tiny

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

@Composable
fun Modifier.surfaceSection(
    backgroundColor: Color = AppTheme.colors.surface,
    borderColor: Color = AppTheme.colors.textSecondary,
    borderStroke: BorderStroke = BorderStroke(1.dp, borderColor),
    shape: Shape = RoundedCornerShape(16.dp),
    onClick: (() -> Unit)? = null,
): Modifier {
    return this
        .background(
            color = backgroundColor,
            shape = shape
        )
        .clip(shape)
        .border(
            border = borderStroke,
            shape = shape
        )
        .optional(onClick) { clickable(onClick = it) }
}