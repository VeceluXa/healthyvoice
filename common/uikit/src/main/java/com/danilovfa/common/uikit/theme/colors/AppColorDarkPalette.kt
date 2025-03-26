package com.danilovfa.common.uikit.theme.colors

import androidx.compose.foundation.text.selection.TextSelectionColors

internal val appColorDarkPalette: AppThemeColors = AppThemeColors(
    background = AppColor.black_80,
    onBackground = AppColor.white_100,

    surface = AppColor.grey_80,
    surfaceDisabled = AppColor.grey_100,
    onSurface = AppColor.white_100,

    primary = AppColor.green_40,
    onPrimary = AppColor.black_100,
    primaryDisabled = AppColor.green_60,

    secondary = AppColor.blue_70,
    onSecondary = AppColor.black_100,
    secondaryDisabled = AppColor.blue_100,

    textPrimary = AppColor.white_100,
    textSecondary = AppColor.white_80,
    textDisabled = AppColor.white_60,

    success = AppColor.green_100,
    error = AppColor.red,
    onError = AppColor.white_100,
    indicator = AppColor.yellow,
    indicatorSecondary = AppColor.purple,

    divider = AppColor.grey_10,
)

val appTextSelectionColorsDarkPalette = TextSelectionColors(
    handleColor = AppColor.green_40,
    backgroundColor = AppColor.green_40.copy(alpha = 0.5f)
)