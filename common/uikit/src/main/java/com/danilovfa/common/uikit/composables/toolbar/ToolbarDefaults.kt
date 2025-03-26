package com.danilovfa.common.uikit.composables.toolbar

import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import com.danilovfa.common.uikit.theme.AppTheme

@Immutable
data class AppToolbarColors(
    val backgroundColor: Color,
    val contentColor: Color,
    val buttonColor: Color,
    val buttonDisabledColor: Color
) {
    @Composable
    fun buttonColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) buttonColor else buttonDisabledColor)
    }

    companion object {
        @Composable
        fun primaryToolbarColors(
            backgroundColor: Color = AppTheme.colors.background,
            contentColor: Color = AppTheme.colors.onBackground,
            buttonColor: Color = AppTheme.colors.primary,
            buttonDisabledColor: Color = AppTheme.colors.primaryDisabled
        ): AppToolbarColors = AppToolbarColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            buttonColor = buttonColor,
            buttonDisabledColor = buttonDisabledColor,
        )

        @Composable
        fun secondaryToolbarColors(
            backgroundColor: Color = AppTheme.colors.secondary,
            contentColor: Color = AppTheme.colors.onSecondary,
            buttonColor: Color = AppTheme.colors.primary,
            buttonDisabledColor: Color = AppTheme.colors.primaryDisabled
        ): AppToolbarColors = AppToolbarColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            buttonColor = buttonColor,
            buttonDisabledColor = buttonDisabledColor,
        )
    }

    fun toIconButtonColors() = IconButtonColors(
        containerColor = Color.Transparent,
        contentColor = contentColor,
        disabledContentColor = buttonDisabledColor,
        disabledContainerColor = Color.Transparent
    )
}