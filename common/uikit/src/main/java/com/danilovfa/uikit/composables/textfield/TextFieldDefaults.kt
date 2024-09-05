package com.danilovfa.uikit.composables.textfield

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
import com.danilovfa.uikit.theme.AppTheme

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
    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("LongParameterList")
    @Composable
    fun textFieldColors(
        textColor: Color = AppTheme.colors.textPrimary,
        disabledTextColor: Color = AppTheme.colors.textSecondary,
        backgroundColor: Color = Color.Transparent,
        cursorColor: Color = AppTheme.colors.buttonPrimary,
        errorCursorColor: Color = AppTheme.colors.error,
        focusedIndicatorColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        unfocusedIndicatorColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        disabledIndicatorColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        errorIndicatorColor: Color = AppTheme.colors.error,
        leadingIconColor: Color = AppTheme.colors.buttonPrimary,
        disabledLeadingIconColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        errorLeadingIconColor: Color = AppTheme.colors.error,
        trailingIconColor: Color = AppTheme.colors.buttonPrimary,
        disabledTrailingIconColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        errorTrailingIconColor: Color = AppTheme.colors.error,
        focusedLabelColor: Color = AppTheme.colors.textPrimary,
        unfocusedLabelColor: Color = AppTheme.colors.textSecondary,
        disabledLabelColor: Color = AppTheme.colors.textSecondary,
        errorLabelColor: Color = AppTheme.colors.error,
        placeholderColor: Color = AppTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AppTheme.colors.buttonPrimaryDisabled,
    ): TextFieldColors = TextFieldDefaults.textFieldColors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = disabledTextColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        focusedLeadingIconColor = leadingIconColor,
        unfocusedLeadingIconColor = leadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = trailingIconColor,
        unfocusedTrailingIconColor = trailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        containerColor = backgroundColor,
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
    fun outlinedTextFieldColors(
        textColor: Color = AppTheme.colors.textPrimary,
        disabledTextColor: Color = AppTheme.colors.textSecondary,
        backgroundColor: Color = Color.Transparent,
        cursorColor: Color = AppTheme.colors.buttonPrimary,
        errorCursorColor: Color = AppTheme.colors.error,
        focusedBorderColor: Color = AppTheme.colors.buttonPrimary,
        unfocusedBorderColor: Color = AppTheme.colors.buttonPrimary,
        disabledBorderColor: Color = AppTheme.colors.buttonPrimary,
        errorBorderColor: Color = AppTheme.colors.buttonPrimary,
        leadingIconColor: Color = AppTheme.colors.buttonPrimary,
        disabledLeadingIconColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        errorLeadingIconColor: Color = AppTheme.colors.error,
        trailingIconColor: Color = AppTheme.colors.buttonPrimary,
        disabledTrailingIconColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        errorTrailingIconColor: Color = AppTheme.colors.error,
        focusedLabelColor: Color = AppTheme.colors.textPrimary,
        unfocusedLabelColor: Color = AppTheme.colors.textSecondary,
        disabledLabelColor: Color = AppTheme.colors.textSecondary,
        errorLabelColor: Color = AppTheme.colors.error,
        placeholderColor: Color = AppTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AppTheme.colors.buttonPrimaryDisabled,
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("LongParameterList")
    @Composable
    fun transparentTextFieldColors(
        textColor: Color = AppTheme.colors.textPrimary,
        disabledTextColor: Color = AppTheme.colors.textSecondary,
        backgroundColor: Color = Color.Transparent,
        cursorColor: Color = AppTheme.colors.buttonPrimary,
        errorCursorColor: Color = AppTheme.colors.error,
        focusedBorderColor: Color = Color.Transparent,
        unfocusedBorderColor: Color = Color.Transparent,
        disabledBorderColor: Color = Color.Transparent,
        errorBorderColor: Color = Color.Transparent,
        leadingIconColor: Color = AppTheme.colors.buttonPrimary,
        disabledLeadingIconColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        errorLeadingIconColor: Color = AppTheme.colors.error,
        trailingIconColor: Color = AppTheme.colors.buttonPrimary,
        disabledTrailingIconColor: Color = AppTheme.colors.buttonPrimaryDisabled,
        errorTrailingIconColor: Color = AppTheme.colors.error,
        focusedLabelColor: Color = AppTheme.colors.textPrimary,
        unfocusedLabelColor: Color = AppTheme.colors.textSecondary,
        disabledLabelColor: Color = AppTheme.colors.textSecondary,
        errorLabelColor: Color = AppTheme.colors.error,
        placeholderColor: Color = AppTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AppTheme.colors.buttonPrimaryDisabled,
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