package com.danilovfa.common.uikit.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getMaterialTheme(colors: AppThemeColors) = ColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primary,
    onPrimaryContainer = colors.onPrimary,
    inversePrimary = colors.primaryDisabled,
    secondary = colors.textSecondary,
    onSecondary = colors.onSecondary,
    secondaryContainer = colors.secondary,
    onSecondaryContainer = colors.secondaryDisabled,
    tertiary = Color.Unspecified,
    onTertiary = Color.Unspecified,
    tertiaryContainer = Color.Unspecified,
    onTertiaryContainer = Color.Unspecified,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surface,
    onSurfaceVariant = colors.onSurface,
    surfaceTint = colors.surface,
    inverseSurface = colors.surface,
    inverseOnSurface = colors.onSurface,
    error = colors.error,
    onError = colors.error,
    errorContainer = colors.error,
    onErrorContainer = colors.error,
    outline = colors.divider,
    outlineVariant = colors.divider,
    scrim = colors.divider,
    surfaceBright = colors.surface,
    surfaceDim = colors.surface,
    surfaceContainer = colors.surface,
    surfaceContainerHigh = colors.surface,
    surfaceContainerHighest = colors.surface,
    surfaceContainerLow = colors.surface,
    surfaceContainerLowest = colors.surface,
)