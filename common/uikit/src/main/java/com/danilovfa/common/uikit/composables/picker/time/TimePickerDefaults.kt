package com.danilovfa.common.uikit.composables.picker.time

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import com.danilovfa.common.uikit.theme.AppTheme

object AppTimePickerDefaults {
    @OptIn(ExperimentalMaterial3Api::class)
    val primary @Composable get() = TimePickerDefaults.colors(
        clockDialColor = AppTheme.colors.primary,
        clockDialSelectedContentColor = AppTheme.colors.primary,
        clockDialUnselectedContentColor = AppTheme.colors.primary,
        periodSelectorSelectedContainerColor = AppTheme.colors.primary,
        periodSelectorUnselectedContainerColor = AppTheme.colors.primary,

        selectorColor = AppTheme.colors.primary,
        containerColor = AppTheme.colors.surface,
        periodSelectorBorderColor = AppTheme.colors.textSecondary,
        periodSelectorSelectedContentColor = AppTheme.colors.primary,
        periodSelectorUnselectedContentColor = AppTheme.colors.primary,
        timeSelectorSelectedContainerColor = AppTheme.colors.background,
        timeSelectorUnselectedContainerColor = AppTheme.colors.background,
        timeSelectorSelectedContentColor = AppTheme.colors.textPrimary,
        timeSelectorUnselectedContentColor = AppTheme.colors.textDisabled
    )
}