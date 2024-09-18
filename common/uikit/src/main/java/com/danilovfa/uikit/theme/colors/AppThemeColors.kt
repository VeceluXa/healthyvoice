package com.danilovfa.uikit.theme.colors

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class AppThemeColors(
    val backgroundPrimary: Color,
    val backgroundOnPrimary: Color,
    val backgroundSecondary: Color,
    val backgroundOnSecondary: Color,
    val backgroundTertiary: Color,
    val backgroundOnTertiary: Color,
    val surface: Color,
    val onSurface: Color,

    val buttonPrimary: Color,
    val buttonOnPrimary: Color,
    val buttonPrimaryDisabled: Color,
    val buttonSecondary: Color,
    val buttonOnSecondary: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val success: Color,
    val surfaceSuccess: Color,
    val onSurfaceSuccess: Color,
    val error: Color,
    val indicator: Color,

    val toggleBorderSelected: Color,
    val toggleBorderUnselected: Color,
    val toggleContainerSelected: Color,
    val toggleContainerUnselected: Color,
    val toggleSelectorSelected: Color,
    val toggleSelectorUnselected: Color,

    val divider: Color,

    val shimmerGradient: GradientColor,

    val graphSelect: Color
)

data class GradientColor(
    val colorStart: Color,
    val colorCenter: Color,
    val colorEnd: Color,
)