package com.danilovfa.common.uikit.composables.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import com.danilovfa.common.uikit.theme.AppTheme

internal const val AnimationDuration = 150

@Composable
fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledTextColor
        isError -> errorTextColor
        focused -> focusedTextColor
        else -> unfocusedTextColor
    }
    return rememberUpdatedState(targetValue)
}

fun TextFieldColors.cursorColor(
    isError: Boolean = false
) = if (isError) errorCursorColor else cursorColor

@Composable
fun TextFieldColors.containerColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledContainerColor
        isError -> errorContainerColor
        focused -> focusedContainerColor
        else -> unfocusedContainerColor
    }
    return animateColorAsState(targetValue, tween(durationMillis = AnimationDuration), label = "TextFieldContainerColor")
}

@Composable
fun TextFieldColors.indicatorColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledIndicatorColor
        isError -> errorIndicatorColor
        focused -> focusedIndicatorColor
        else -> unfocusedIndicatorColor
    }
    return if (enabled) {
        animateColorAsState(targetValue, tween(durationMillis = AnimationDuration), label = "TextFieldIndicator")
    } else {
        rememberUpdatedState(targetValue)
    }
}

object AppTextFieldDefaults {
    @Suppress("LongParameterList")
    @Composable
    fun textFieldColors(
        textColor: Color = AppTheme.colors.textPrimary,
        disabledTextColor: Color = AppTheme.colors.textSecondary,
        backgroundColor: Color = AppTheme.colors.surface,
        cursorColor: Color = AppTheme.colors.primary,
        errorCursorColor: Color = AppTheme.colors.error,
        focusedIndicatorColor: Color = AppTheme.colors.primaryDisabled,
        unfocusedIndicatorColor: Color = AppTheme.colors.primaryDisabled,
        disabledIndicatorColor: Color = AppTheme.colors.primaryDisabled,
        errorIndicatorColor: Color = AppTheme.colors.error,
        leadingIconColor: Color = AppTheme.colors.primary,
        disabledLeadingIconColor: Color = AppTheme.colors.primaryDisabled,
        errorLeadingIconColor: Color = AppTheme.colors.error,
        trailingIconColor: Color = AppTheme.colors.primary,
        disabledTrailingIconColor: Color = AppTheme.colors.primaryDisabled,
        errorTrailingIconColor: Color = AppTheme.colors.error,
        focusedLabelColor: Color = AppTheme.colors.textPrimary,
        unfocusedLabelColor: Color = AppTheme.colors.textSecondary,
        disabledLabelColor: Color = AppTheme.colors.textSecondary,
        errorLabelColor: Color = AppTheme.colors.error,
        placeholderColor: Color = AppTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AppTheme.colors.primaryDisabled,
    ): TextFieldColors = TextFieldDefaults.colors().copy(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = disabledTextColor,
        errorTextColor = errorLabelColor,
        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,
        disabledContainerColor = backgroundColor,
        errorContainerColor = backgroundColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
        focusedLeadingIconColor = leadingIconColor,
        unfocusedLeadingIconColor = leadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = trailingIconColor,
        unfocusedTrailingIconColor = trailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        errorPlaceholderColor = errorLabelColor,
    )

    @Suppress("LongParameterList")
    @Composable
    fun outlinedTextFieldColors(
        textColor: Color = AppTheme.colors.textPrimary,
        disabledTextColor: Color = AppTheme.colors.textSecondary,
        backgroundColor: Color = Color.Transparent,
        cursorColor: Color = AppTheme.colors.primary,
        errorCursorColor: Color = AppTheme.colors.error,
        focusedBorderColor: Color = AppTheme.colors.primary,
        unfocusedBorderColor: Color = AppTheme.colors.primary,
        disabledBorderColor: Color = AppTheme.colors.primary,
        errorBorderColor: Color = AppTheme.colors.primary,
        leadingIconColor: Color = AppTheme.colors.primary,
        disabledLeadingIconColor: Color = AppTheme.colors.primaryDisabled,
        errorLeadingIconColor: Color = AppTheme.colors.error,
        trailingIconColor: Color = AppTheme.colors.primary,
        disabledTrailingIconColor: Color = AppTheme.colors.primaryDisabled,
        errorTrailingIconColor: Color = AppTheme.colors.error,
        focusedLabelColor: Color = AppTheme.colors.textPrimary,
        unfocusedLabelColor: Color = AppTheme.colors.textSecondary,
        disabledLabelColor: Color = AppTheme.colors.textSecondary,
        errorLabelColor: Color = AppTheme.colors.error,
        placeholderColor: Color = AppTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AppTheme.colors.primaryDisabled,
    ): TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = disabledTextColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        focusedIndicatorColor = focusedBorderColor,
        unfocusedIndicatorColor = unfocusedBorderColor,
        errorIndicatorColor = disabledBorderColor,
        disabledIndicatorColor = errorBorderColor,
        focusedLeadingIconColor = leadingIconColor,
        unfocusedLeadingIconColor = leadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = trailingIconColor,
        unfocusedTrailingIconColor = trailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor
    )

    @Suppress("LongParameterList")
    @Composable
    fun transparentTextFieldColors(
        textColor: Color = AppTheme.colors.textPrimary,
        disabledTextColor: Color = AppTheme.colors.textSecondary,
        backgroundColor: Color = Color.Transparent,
        cursorColor: Color = AppTheme.colors.primary,
        errorCursorColor: Color = AppTheme.colors.error,
        focusedBorderColor: Color = Color.Transparent,
        unfocusedBorderColor: Color = Color.Transparent,
        disabledBorderColor: Color = Color.Transparent,
        errorBorderColor: Color = Color.Transparent,
        leadingIconColor: Color = AppTheme.colors.primary,
        disabledLeadingIconColor: Color = AppTheme.colors.primaryDisabled,
        errorLeadingIconColor: Color = AppTheme.colors.error,
        trailingIconColor: Color = AppTheme.colors.primary,
        disabledTrailingIconColor: Color = AppTheme.colors.primaryDisabled,
        errorTrailingIconColor: Color = AppTheme.colors.error,
        focusedLabelColor: Color = AppTheme.colors.textPrimary,
        unfocusedLabelColor: Color = AppTheme.colors.textSecondary,
        disabledLabelColor: Color = AppTheme.colors.textSecondary,
        errorLabelColor: Color = AppTheme.colors.error,
        placeholderColor: Color = AppTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AppTheme.colors.primaryDisabled,
    ): TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = disabledTextColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        focusedIndicatorColor = focusedBorderColor,
        unfocusedIndicatorColor = unfocusedBorderColor,
        disabledIndicatorColor = disabledBorderColor,
        errorIndicatorColor = errorBorderColor,
        focusedLeadingIconColor = leadingIconColor,
        unfocusedLeadingIconColor = leadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = trailingIconColor,
        unfocusedTrailingIconColor = trailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor
    )
}