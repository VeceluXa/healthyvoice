package com.danilovfa.common.uikit.composables.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.theme.AppTheme

internal val LargeButtonHeight = 56.dp
internal val ButtonHeight = 48.dp
val LargeButtonContentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
val SmallButtonContentPadding: PaddingValues = PaddingValues(vertical = 8.dp, horizontal = 12.dp)

object AppButtonColors {
    @Composable
    fun primaryButtonColors(
        backgroundColor: Color = AppTheme.colors.primary,
        contentColor: Color = AppTheme.colors.onPrimary,
        disabledBackgroundColor: Color = AppTheme.colors.primaryDisabled,
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
        contentColor: Color = AppTheme.colors.primary,
    ): ButtonColors = ButtonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = backgroundColor,
        disabledContentColor = contentColor,
    )

    @Composable
    fun textButtonColors(
        backgroundColor: Color = Color.Transparent,
        contentColor: Color = AppTheme.colors.primary,
        disabledContentColor: Color = AppTheme.colors.textDisabled,
    ): ButtonColors = ButtonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = backgroundColor,
        disabledContentColor = disabledContentColor,
    )
}

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