package com.danilovfa.common.uikit.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.danilovfa.common.uikit.theme.colors.AppThemeColors
import com.danilovfa.common.uikit.theme.colors.appColorLightPalette
import com.danilovfa.common.uikit.theme.colors.appColorDarkPalette
import com.danilovfa.common.uikit.theme.colors.appTextSelectionColorsDarkPalette
import com.danilovfa.common.uikit.theme.colors.appTextSelectionColorsLightPalette
import com.danilovfa.common.uikit.theme.colors.getMaterialTheme

@Composable
fun AppTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorPalette: AppThemeColors = remember(useDarkTheme) {
        if (!useDarkTheme) appColorLightPalette else appColorDarkPalette
    }

    val textSelectionColors: TextSelectionColors = remember(useDarkTheme) {
        if (!useDarkTheme) appTextSelectionColorsLightPalette else appTextSelectionColorsDarkPalette
    }

    SystemBars(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        isStatusBarLight = !useDarkTheme,
        isNavigationBarLight = !useDarkTheme
    )

    MaterialTheme(
        typography = materialTypography,
        colorScheme = getMaterialTheme(colorPalette),
        shapes = shapes
    ) {
        CompositionLocalProvider(
            LocalAppColors provides colorPalette,
            LocalTextStyle provides AppTypography.bodyRegular14,
            LocalTextSelectionColors provides textSelectionColors,
            LocalContentColor provides colorPalette.textPrimary,
            content = content
        )
    }
}

internal val LocalAppColors = staticCompositionLocalOf<AppThemeColors> {
    error("No AppThemeColors provided")
}