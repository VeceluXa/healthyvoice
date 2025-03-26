@file:OptIn(ExperimentalMaterial3Api::class)

package com.danilovfa.common.uikit.composables.picker.time

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerSelectionMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

private val DisplaySeparatorWidth = 24.dp
val PeriodSelectorContainerHeight = 72.0.dp

@Composable
fun TimeInput(
    colors: TimePickerColors,
    state: TimePickerState,
    modifier: Modifier = Modifier,
    maxHours: Int = 24,
) {
    var hourValue by
    rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = state.hour.toString().padStart(2, '0')))
    }
    var minuteValue by
    rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = state.minute.toString().padStart(2, '0')))
    }
    Row(
        modifier = modifier.padding(bottom = AppDimension.layoutMainMargin),
        verticalAlignment = Alignment.Top
    ) {
        val textStyle =
            AppTypography.titleMedium22.copy(
                textAlign = TextAlign.Center,
                color = colors.timeSelectorContentColor(true)
            )

        CompositionLocalProvider(
            LocalTextStyle provides textStyle,
            // Always display the time input text field from left to right.
            LocalLayoutDirection provides LayoutDirection.Ltr
        ) {
            Row {
                TimePickerTextField(
                    modifier =
                    Modifier.onKeyEvent { event ->
                        // Zero == 48, Nine == 57
                        val switchFocus =
                            event.utf16CodePoint in 48..57 &&
                                    hourValue.selection.start == 2 &&
                                    hourValue.text.length == 2

                        if (switchFocus) {
                            state.selection = TimePickerSelectionMode.Minute
                        }

                        false
                    },
                    value = hourValue,
                    onValueChange = { newValue ->
                        timeInputOnChange(
                            selection = TimePickerSelectionMode.Hour,
                            state = state,
                            value = newValue,
                            prevValue = hourValue,
                            max = maxHours,
                        ) {
                            hourValue = it
                        }
                    },
                    state = state,
                    selection = TimePickerSelectionMode.Hour,
                    keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions =
                    KeyboardActions(
                        onNext = { state.selection = TimePickerSelectionMode.Minute }
                    ),
                    colors = colors,
                )
                DisplaySeparator(
                    Modifier.size(
                        DisplaySeparatorWidth,
                        PeriodSelectorContainerHeight
                    )
                )
                TimePickerTextField(
                    modifier =
                    Modifier.onPreviewKeyEvent { event ->
                        // 0 == KEYCODE_DEL
                        val switchFocus =
                            event.utf16CodePoint == 0 && minuteValue.selection.start == 0

                        if (switchFocus) {
                            state.selection = TimePickerSelectionMode.Hour
                        }

                        switchFocus
                    },
                    value = minuteValue,
                    onValueChange = { newValue ->
                        timeInputOnChange(
                            selection = TimePickerSelectionMode.Minute,
                            state = state,
                            value = newValue,
                            prevValue = minuteValue,
                            max = 59,
                        ) {
                            minuteValue = it
                        }
                    },
                    state = state,
                    selection = TimePickerSelectionMode.Minute,
                    keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions =
                    KeyboardActions(
                        onNext = { state.selection = TimePickerSelectionMode.Minute }
                    ),
                    colors = colors,
                )
            }
        }
    }
}

private fun timeInputOnChange(
    selection: TimePickerSelectionMode,
    state: TimePickerState,
    value: TextFieldValue,
    prevValue: TextFieldValue,
    max: Int,
    onNewValue: (value: TextFieldValue) -> Unit
) {
    if (value.text == prevValue.text) {
        // just selection change
        onNewValue(value)
        return
    }

    if (value.text.isEmpty()) {
        if (selection == TimePickerSelectionMode.Hour) {
            state.hour = 0
        } else {
            state.minute = 0
        }
        onNewValue(value.copy(text = ""))
        return
    }

    try {
        val newValue =
            if (value.text.length == 3 && value.selection.start == 1) {
                value.text[0].digitToInt()
            } else {
                value.text.toInt()
            }

        if (newValue <= max) {
            if (selection == TimePickerSelectionMode.Hour) {
                state.hour = newValue
            } else {
                state.minute = newValue
            }

            onNewValue(
                if (value.text.length <= 2) {
                    value
                } else {
                    value.copy(text = value.text[0].toString())
                }
            )
        }
    } catch (_: NumberFormatException) {
    } catch (_: IllegalArgumentException) {
        // do nothing no state update
    }
}

@Composable
private fun DisplaySeparator(modifier: Modifier) {
    val style =
        LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            lineHeightStyle =
            LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )

    Box(modifier = modifier.clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
        Text(text = ":", color = AppTheme.colors.onSurface, style = style)
    }
}