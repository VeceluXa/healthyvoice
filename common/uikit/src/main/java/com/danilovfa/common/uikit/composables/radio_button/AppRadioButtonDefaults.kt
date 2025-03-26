package com.danilovfa.common.uikit.composables.radio_button

import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import com.danilovfa.common.uikit.theme.AppTheme

object AppRadioButtonDefaults {
    val primary @Composable get() = RadioButtonDefaults.colors(
        selectedColor = AppTheme.colors.primary,
        unselectedColor = AppTheme.colors.primaryDisabled,
        disabledSelectedColor = AppTheme.colors.primary,
        disabledUnselectedColor = AppTheme.colors.textPrimary
    )
}