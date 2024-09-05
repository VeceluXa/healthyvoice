package com.danilovfa.uikit.composables.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.CheckboxColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.danilovfa.uikit.theme.AppTheme
import androidx.compose.material3.Checkbox as MaterialCheckbox

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = AppCheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    MaterialCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}

object AppCheckboxDefaults {

    @Composable
    fun colors() = CheckboxColors(
        checkedCheckmarkColor = AppTheme.colors.backgroundPrimary,
        uncheckedCheckmarkColor = Color.Transparent,
        checkedBoxColor = AppTheme.colors.buttonPrimary,
        uncheckedBoxColor = Color.Transparent,
        disabledCheckedBoxColor = AppTheme.colors.buttonPrimary.copy(alpha = 0.1f),
        disabledUncheckedBoxColor = Color.Transparent,
        disabledIndeterminateBoxColor = AppTheme.colors.buttonPrimaryDisabled.copy(alpha = 0.8f),
        checkedBorderColor = AppTheme.colors.buttonPrimary,
        uncheckedBorderColor = AppTheme.colors.buttonPrimary,
        disabledBorderColor = AppTheme.colors.buttonPrimary,
        disabledUncheckedBorderColor = AppTheme.colors.buttonPrimary,
        disabledIndeterminateBorderColor = AppTheme.colors.buttonPrimary
    )
}