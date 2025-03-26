package com.danilovfa.common.uikit.composables.radio_button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import androidx.compose.material3.RadioButton as MaterialRadioButton

@Composable
fun RadioButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = AppRadioButtonDefaults.primary
) {
    MaterialRadioButton(
        selected = isSelected,
        onClick = onClick,
        enabled = enabled && isSelected.not(),
        colors = colors,
        modifier = modifier
    )
}

@Composable
fun RadioButtonRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(),
    colors: RadioButtonColors = AppRadioButtonDefaults.primary,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    textColor: Color = AppTheme.colors.textSecondary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled && isSelected.not()
            )
            .padding(paddingValues)
    ) {
        RadioButton(
            isSelected = isSelected,
            onClick = onClick,
            enabled = enabled,
            colors = colors
        )

        HSpacer(AppDimension.layoutMediumMargin)

        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}