package com.danilovfa.common.uikit.composables.checkbox

import androidx.compose.material3.CheckboxColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.danilovfa.common.uikit.theme.AppTheme

object AppCheckboxDefaults {
    val primary @Composable get() = CheckboxColors(
        checkedCheckmarkColor = AppTheme.colors.primary,
        uncheckedCheckmarkColor = Color.Transparent,
        checkedBoxColor = Color.Transparent,
        uncheckedBoxColor = Color.Transparent,
        disabledCheckedBoxColor = Color.Transparent,
        disabledUncheckedBoxColor = Color.Transparent,
        disabledIndeterminateBoxColor = Color.Transparent,
        checkedBorderColor = AppTheme.colors.primary,
        uncheckedBorderColor = AppTheme.colors.textSecondary,
        disabledBorderColor = AppTheme.colors.primaryDisabled,
        disabledUncheckedBorderColor = AppTheme.colors.primaryDisabled,
        disabledIndeterminateBorderColor = AppTheme.colors.textSecondary,
    )
}