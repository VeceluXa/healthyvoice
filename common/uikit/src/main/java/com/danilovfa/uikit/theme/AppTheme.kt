package com.danilovfa.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.danilovfa.uikit.theme.colors.AppThemeColors

object AppTheme {
    val colors: AppThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}