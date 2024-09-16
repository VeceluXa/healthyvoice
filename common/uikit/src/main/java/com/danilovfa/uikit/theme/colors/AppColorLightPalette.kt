package com.danilovfa.uikit.theme.colors

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.Color

val appColorLightPalette: AppThemeColors = AppThemeColors(
    backgroundPrimary = AppColor.white,
    backgroundOnPrimary = AppColor.black,
    backgroundSecondary = AppColor.green_80,
    backgroundOnSecondary = AppColor.black,
    backgroundTertiary = Color.Black,
    backgroundOnTertiary = AppColor.white,
    surface = AppColor.grey_10,
    onSurface = AppColor.black,

    buttonPrimary = AppColor.black,
    buttonOnPrimary = AppColor.white,
    buttonPrimaryDisabled = AppColor.grey_60,
    buttonSecondary = AppColor.green_80,
    buttonOnSecondary = AppColor.black,

    textPrimary = AppColor.black,
    textSecondary = AppColor.grey_100,
    textTertiary = AppColor.white,
    textDisabled = AppColor.grey_90,
    success = AppColor.green_50,
    surfaceSuccess = AppColor.green_20.copy(alpha = 0.15f),
    onSurfaceSuccess = AppColor.green_100,
    error = AppColor.red_100,
    indicator = AppColor.blue,

    toggleBorderSelected = AppColor.black,
    toggleBorderUnselected = AppColor.grey_80,
    toggleContainerSelected = AppColor.black,
    toggleContainerUnselected = AppColor.grey_40,
    toggleSelectorSelected = AppColor.white,
    toggleSelectorUnselected = AppColor.grey_80,

    divider = AppColor.grey_50,
    shimmerGradient = GradientColor(
        colorStart = AppColor.grey_40,
        colorCenter = AppColor.grey_50,
        colorEnd = AppColor.grey_80,
    ),

    mapRouteColor = AppColor.blue
)

val appTextSelectionColorsLightPalette = TextSelectionColors(
    handleColor = AppColor.yellow,
    backgroundColor = AppColor.yellow.copy(alpha = 0.5f)
)