package com.danilovfa.uikit.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.danilovfa.uikit.theme.colors.AppThemeColors
import com.danilovfa.uikit.theme.colors.appColorLightPalette
import com.danilovfa.uikit.theme.colors.appTextSelectionColorsLightPalette

@Composable
fun AppTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorPalette: AppThemeColors = remember(useDarkTheme) {
        if (!useDarkTheme) appColorLightPalette else appColorLightPalette
    }

    val textSelectionColors: TextSelectionColors = remember(useDarkTheme) {
        if (!useDarkTheme) appTextSelectionColorsLightPalette else appTextSelectionColorsLightPalette
    }

    SystemBars(
        statusBarColor = colorPalette.backgroundPrimary,
        navigationBarColor = colorPalette.backgroundPrimary
    )

    MaterialTheme(
        typography = materialTypography,
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