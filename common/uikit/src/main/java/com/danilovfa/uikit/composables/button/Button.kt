package com.danilovfa.uikit.composables.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.resources.drawable.AppIcon
import com.danilovfa.uikit.composables.HSpacer
import com.danilovfa.uikit.composables.VSpacer
import com.danilovfa.uikit.composables.text.Text
import com.danilovfa.uikit.modifier.consumeTouches
import com.danilovfa.uikit.modifier.rememberMinSize
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography
import com.danilovfa.uikit.theme.micro
import com.danilovfa.uikit.theme.tiny
import androidx.compose.material3.TextButton as MaterialTextButton

@Composable
fun PrimaryButtonLarge(
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
                indication = rememberRipple(radius = 10.dp)
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
fun OutlineButtonLarge(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = AppButtonColors.outlineButtonColors(),
    maxLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.tiny,
    contentPadding: PaddingValues = LargeButtonContentPadding,
    loading: Boolean = false,
    icon: Painter? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = LargeButtonHeight)
            .consumeTouches(loading)
            .rememberMinSize { _, _ -> !loading },
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
        border = BorderStroke(width = 1.dp, color = colors.contentColor(enabled)),
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
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = colors.contentColor(enabled)
                    )
                    HSpacer(8.dp)
                }
                Text(
                    text = text,
                    maxLines = maxLines,
                    textAlign = TextAlign.Center,
                    style = AppTypography.bodyRegular14,
                )
            }
        }
    }
}

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
    MaterialTextButton(
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

@Composable
fun OutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    colors: ButtonColors = AppButtonColors.outlineButtonColors(),
    maxLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.tiny,
    contentPadding: PaddingValues = LargeButtonContentPadding,
    loading: Boolean = false,
    icon: Painter? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = LargeButtonHeight)
            .consumeTouches(loading)
            .rememberMinSize { _, _ -> !loading },
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
        border = BorderStroke(width = 1.dp, color = colors.contentColor(enabled)),
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
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = colors.contentColor(enabled)
                    )
                    HSpacer(8.dp)
                }
                Text(
                    text = text,
                    maxLines = maxLines,
                    textAlign = TextAlign.Center,
                    style = textStyle,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("LongMethod", "StringLiteralDuplication")
private fun ButtonsPreview() {
    AppTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(AppTheme.colors.backgroundPrimary)
        ) {
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
            )
            VSpacer(8.dp)
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
                loading = true,
            )
            VSpacer(8.dp)
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
                icon = AppIcon.ArrowLeft
            )
            VSpacer(8.dp)
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
                enabled = false
            )
            VSpacer(8.dp)
            OutlineButtonLarge(
                text = "OutlineButtonLarge",
                onClick = {},
            )
            VSpacer(8.dp)
            OutlineButtonLarge(
                text = "OutlineButtonLarge",
                onClick = {},
                loading = true,
            )
            VSpacer(8.dp)
            OutlineButtonLarge(
                text = "OutlineButtonLarge",
                onClick = {},
                icon = AppIcon.ArrowLeft,
            )
            VSpacer(8.dp)
            TextButton(
                text = "TextButton",
                onClick = {},
            )
            VSpacer(8.dp)
            TextButton(
                text = "TextButton",
                onClick = {},
                loading = true
            )
            VSpacer(8.dp)
            TextButton(
                text = "TextButton",
                onClick = {},
                icon = AppIcon.ArrowLeft,
            )
            VSpacer(8.dp)
            FloatTextButton(
                text = "FloatTextButton",
                onClick = {},
                icon = AppIcon.ArrowLeft,
            )
            VSpacer(8.dp)
        }
    }
}

private val LargeButtonHeight = 56.dp
val LargeButtonContentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
val SmallButtonContentPadding: PaddingValues = PaddingValues(vertical = 8.dp, horizontal = 12.dp)

fun ButtonColors.contentColor(enabled: Boolean): Color =
    if (enabled) contentColor else disabledContentColor

fun ButtonColors.containerColor(enabled: Boolean): Color =
    if (enabled) containerColor else disabledContainerColor

object AppButtonDefaults {
    private val TextButtonHorizontalPadding = 12.dp
    val SmallTextButtonPadding @Composable get() = PaddingValues(
        start = TextButtonHorizontalPadding,
        end = TextButtonHorizontalPadding
    )
}

object AppButtonColors {
    @Composable
    fun primaryButtonColors(
        backgroundColor: Color = AppTheme.colors.buttonPrimary,
        contentColor: Color = AppTheme.colors.buttonOnPrimary,
        disabledBackgroundColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        disabledContentColor: Color = contentColor,
    ): ButtonColors = ButtonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = disabledBackgroundColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun secondaryButtonColor(
        backgroundColor: Color = AppTheme.colors.buttonSecondary,
        contentColor: Color = AppTheme.colors.buttonOnSecondary,
        disabledBackgroundColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        disabledContentColor: Color = contentColor,
    ): ButtonColors = ButtonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = disabledBackgroundColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun tertiaryButtonColor(
        backgroundColor: Color = AppTheme.colors.backgroundPrimary,
        contentColor: Color = AppTheme.colors.backgroundOnPrimary,
        disabledBackgroundColor: Color = AppTheme.colors.surface,
        disabledContentColor: Color = contentColor,
    ): ButtonColors = ButtonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = disabledBackgroundColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun outlineButtonColors(
        backgroundColor: Color = Color.Transparent,
        contentColor: Color = AppTheme.colors.buttonPrimary,
    ): ButtonColors = ButtonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = backgroundColor,
        disabledContentColor = contentColor,
    )

    @Composable
    fun textButtonColors(
        backgroundColor: Color = Color.Transparent,
        contentColor: Color = AppTheme.colors.buttonPrimary,
        disabledContentColor: Color = AppTheme.colors.textTertiary
            .copy(alpha = 0.38f),
    ): ButtonColors = ButtonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = backgroundColor,
        disabledContentColor = disabledContentColor,
    )
}
