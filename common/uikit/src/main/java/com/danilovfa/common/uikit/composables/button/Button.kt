package com.danilovfa.common.uikit.composables.button

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.consumeTouches
import com.danilovfa.common.uikit.modifier.rememberMinSize
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.common.uikit.theme.tiny

@Composable
fun ButtonLarge(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = AppButtonColors.primaryButtonColors(),
    maxLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.tiny,
    contentPadding: PaddingValues = LargeButtonContentPadding,
    loading: Boolean = false,
    icon: Painter? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = LargeButtonHeight)
            .consumeTouches(loading)
            .rememberMinSize { _, _ -> !loading }
            .indication(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(radius = 10.dp)
            ),
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = colors.contentColor(enabled),
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    maxLines = maxLines,
                    textAlign = TextAlign.Center,
                    style = AppTypography.bodyRegular16,
                    color = colors.contentColor,
                )
                if (icon != null) {
                    HSpacer(8.dp)
                    Icon(
                        painter = icon,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = AppButtonColors.primaryButtonColors(),
    maxLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.tiny,
    contentPadding: PaddingValues = SmallButtonContentPadding,
    loading: Boolean = false,
    icon: Painter? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = ButtonHeight)
            .consumeTouches(loading)
            .rememberMinSize { _, _ -> !loading }
            .indication(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(radius = 10.dp)
            ),
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = colors.contentColor(enabled),
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    maxLines = maxLines,
                    textAlign = TextAlign.Center,
                    style = AppTypography.bodyRegular16,
                    color = colors.contentColor,
                )
                if (icon != null) {
                    HSpacer(8.dp)
                    Icon(
                        painter = icon,
                        contentDescription = null
                    )
                }
            }
        }
    }
}