package com.danilovfa.common.uikit.composables.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.common.uikit.theme.micro
import com.danilovfa.common.uikit.theme.tiny

@Composable
fun TextButtonLarge(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    colors: ButtonColors = AppButtonColors.textButtonColors(),
    shape: Shape = MaterialTheme.shapes.tiny,
    icon: Painter? = null,
) {
    TextButton(
        modifier = modifier.fillMaxWidth(),
        text = text,
        onClick = onClick,
        colors = colors,
        icon = icon,
        iconModifier = Modifier.size(24.dp),
        iconHorizontalSpace = 10.dp,
        shape = shape,
        textStyle = textStyle,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        enabled = enabled,
    )
}

@Composable
fun FloatTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: Painter? = null,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    colors: ButtonColors = AppButtonColors.primaryButtonColors(),
) {
    TextButton(
        modifier = modifier,
        text = text,
        onClick = onClick,
        colors = colors,
        icon = icon,
        iconModifier = Modifier.size(24.dp),
        iconHorizontalSpace = 10.dp,
        shape = RoundedCornerShape(12.dp),
        textStyle = textStyle,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        enabled = enabled,
    )
}

@Suppress("LongParameterList")
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = AppButtonColors.textButtonColors(),
    shape: Shape = MaterialTheme.shapes.micro,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    loading: Boolean = false,
    icon: Painter? = null,
    iconTint: Color = AppButtonColors.textButtonColors().contentColor,
    iconModifier: Modifier? = null,
    iconHorizontalSpace: Dp = 8.dp,
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = colors.contentColor(enabled),
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(contentPadding),
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        tint = iconTint,
                        contentDescription = null,
                        modifier = iconModifier ?: Modifier
                    )
                    HSpacer(iconHorizontalSpace)
                }
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    style = textStyle,
                )
            }
        }
    }
}