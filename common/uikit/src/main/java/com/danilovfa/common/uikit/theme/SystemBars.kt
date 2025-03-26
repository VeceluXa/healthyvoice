package com.danilovfa.common.uikit.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SystemBars(
    colors: SystemBarsColors = SystemBarsDefaults.primary
) {
    SystemBars(
        statusBarColor = colors.statusBarColor,
        navigationBarColor = colors.navigationBarColor,
        isStatusBarLight = colors.isStatusBarLight,
        isNavigationBarLight = colors.isNavigationBarLight
    )
}

@Composable
internal fun SystemBars(
    statusBarColor: Color,
    navigationBarColor: Color,
    isStatusBarLight: Boolean = true,
    isNavigationBarLight: Boolean = true
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            window.statusBarColor = statusBarColor.toArgb() // choose a status bar color
            window.navigationBarColor = navigationBarColor.toArgb() // choose a navigation bar color
            insets.isAppearanceLightStatusBars = isStatusBarLight
            insets.isAppearanceLightNavigationBars = isNavigationBarLight
        }
    }
}

data class SystemBarsColors(
    val statusBarColor: Color,
    val navigationBarColor: Color,
    val isStatusBarLight: Boolean,
    val isNavigationBarLight: Boolean
)

object SystemBarsDefaults {
    val primary: SystemBarsColors @Composable get() = SystemBarsColors(
        statusBarColor = AppTheme.colors.background,
        navigationBarColor = AppTheme.colors.background,
        isStatusBarLight = true,
        isNavigationBarLight = true
    )

    val transparentSystemBars: SystemBarsColors @Composable get() = SystemBarsColors(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        isStatusBarLight = true,
        isNavigationBarLight = true
    )

    val transparentStatusBar: SystemBarsColors @Composable get() = SystemBarsColors(
        statusBarColor = Color.Transparent,
        navigationBarColor = AppTheme.colors.background,
        isStatusBarLight = true,
        isNavigationBarLight = true
    )
}
