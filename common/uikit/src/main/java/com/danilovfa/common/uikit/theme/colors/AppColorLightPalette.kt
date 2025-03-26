package com.danilovfa.common.uikit.theme.colors

import androidx.compose.foundation.text.selection.TextSelectionColors

val appColorLightPalette: AppThemeColors = AppThemeColors(
    background = AppColor.white_100,
    onBackground = AppColor.black_100,

    surface = AppColor.white_80,
    surfaceDisabled = AppColor.grey_10,
    onSurface = AppColor.black_100,

    primary = AppColor.green_100,
    onPrimary = AppColor.white_100,
    primaryDisabled = AppColor.green_20,

    secondary = AppColor.blue_80,
    onSecondary = AppColor.white_100,
    secondaryDisabled = AppColor.blue_10,

    textPrimary = AppColor.black_100,
    textSecondary = AppColor.grey_70,
    textDisabled = AppColor.grey_70,

    success = AppColor.green_100,
    error = AppColor.red,
    onError = AppColor.white_100,
    indicator = AppColor.yellow,
    indicatorSecondary = AppColor.purple,

    divider = AppColor.grey_10,
)

val appTextSelectionColorsLightPalette = TextSelectionColors(
    handleColor = AppColor.green_100,
    backgroundColor = AppColor.green_100.copy(alpha = 0.5f)
)