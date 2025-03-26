package com.danilovfa.common.uikit.theme.colors

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class AppThemeColors(
    val background: Color,
    val onBackground: Color,

    val surface: Color,
    val surfaceDisabled: Color,
    val onSurface: Color,

    val primary: Color,
    val primaryDisabled: Color,
    val onPrimary: Color,

    val secondary: Color,
    val secondaryDisabled: Color,
    val onSecondary: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,

    val success: Color,
    val error: Color,
    val onError: Color,
    val indicator: Color,
    val indicatorSecondary: Color,


    val divider: Color,
)