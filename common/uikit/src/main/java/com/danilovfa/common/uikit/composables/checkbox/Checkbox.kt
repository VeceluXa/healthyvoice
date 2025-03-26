package com.danilovfa.common.uikit.composables.checkbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CheckboxColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import androidx.compose.material3.Checkbox as MaterialCheckbox

@Composable
fun Checkbox(
    checked: Boolean,
    onCheck: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = AppCheckboxDefaults.primary,
    enabled: Boolean = true
) {
    MaterialCheckbox(
        checked = checked,
        onCheckedChange = onCheck,
        enabled = enabled,
        colors = colors,
        modifier = modifier
    )
}

@Composable
fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheck: (Boolean) -> Unit,
    modifier: Modifier,
    colors: CheckboxColors = AppCheckboxDefaults.primary,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    textColor: Color = AppTheme.colors.textSecondary,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = { onCheck(!checked) }
            )
            .padding(paddingValues)
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            colors = colors,
            onCheck = onCheck
        )

        HSpacer(AppDimension.layoutMediumMargin)

        Text(
            text = text,
            style = textStyle,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}