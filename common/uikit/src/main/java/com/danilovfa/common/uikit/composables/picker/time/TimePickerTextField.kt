@file:OptIn(ExperimentalMaterial3Api::class)

package com.danilovfa.common.uikit.composables.picker.time

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerSelectionMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

private const val TIME_FIELD_WIDTH_DP = 96.0
private const val TIME_FIELD_HEIGHT_DP = 72.0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerTextField(
    modifier: Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    state: TimePickerState,
    selection: TimePickerSelectionMode,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    colors: TimePickerColors,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.timeSelectorContainerColor(true),
            unfocusedContainerColor = colors.timeSelectorContainerColor(true),
            focusedTextColor = colors.timeSelectorContentColor(true),
        )
    val selected = selection == state.selection
    Column(modifier = modifier) {
        if (!selected) {
            TimeSelector(
                modifier = Modifier.size(TIME_FIELD_WIDTH_DP.dp, TIME_FIELD_HEIGHT_DP.dp),
                value =
                if (selection == TimePickerSelectionMode.Hour) {
                    state.hour
                } else {
                    state.minute
                },
                state = state,
                selection = selection,
                colors = colors,
            )
        }

        Box(Modifier.visible(selected)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier =
                Modifier
                    .focusRequester(focusRequester)
                    .size(TIME_FIELD_WIDTH_DP.dp, TIME_FIELD_HEIGHT_DP.dp),
                interactionSource = interactionSource,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                textStyle = LocalTextStyle.current,
                enabled = true,
                singleLine = true,
                cursorBrush =
                Brush.verticalGradient(
                    0.00f to Color.Transparent,
                    0.10f to Color.Transparent,
                    0.10f to MaterialTheme.colorScheme.primary,
                    0.90f to MaterialTheme.colorScheme.primary,
                    0.90f to Color.Transparent,
                    1.00f to Color.Transparent
                )
            ) {
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value.text,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = it,
                    singleLine = true,
                    colors = textFieldColors,
                    enabled = true,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            shape = MaterialTheme.shapes.small,
                            colors = textFieldColors,
                        )
                    }
                )
            }
        }
    }

    LaunchedEffect(state.selection) {
        if (state.selection == selection) {
            focusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSelector(
    modifier: Modifier,
    value: Int,
    state: TimePickerState,
    selection: TimePickerSelectionMode,
    colors: TimePickerColors,
) {
    val selected = state.selection == selection

    val containerColor = colors.timeSelectorContainerColor(selected)
    val contentColor = colors.timeSelectorContentColor(selected)
    Surface(
        modifier =
        modifier.semantics(mergeDescendants = true) {
            role = Role.RadioButton
        },
        onClick = {
            if (selection != state.selection) {
                state.selection = selection
            }
        },
        selected = selected,
        shape = MaterialTheme.shapes.small,
        color = containerColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = value.toString().padStart(2, '0'),
                color = contentColor,
            )
        }
    }
}

@Stable
private fun Modifier.visible(visible: Boolean) =
    this.then(
        VisibleModifier(
            visible,
            debugInspectorInfo {
                name = "visible"
                properties["visible"] = visible
            }
        )
    )

private class VisibleModifier(val visible: Boolean, inspectorInfo: InspectorInfo.() -> Unit) :
    LayoutModifier, InspectorValueInfo(inspectorInfo) {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)

        if (!visible) {
            return layout(0, 0) {}
        }
        return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }

    override fun hashCode(): Int = visible.hashCode()

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? VisibleModifier ?: return false
        return visible == otherModifier.visible
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal fun TimePickerColors.timeSelectorContainerColor(selected: Boolean) =
    if (selected) {
        timeSelectorSelectedContainerColor
    } else {
        timeSelectorUnselectedContainerColor
    }

@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal fun TimePickerColors.timeSelectorContentColor(selected: Boolean) =
    if (selected) {
        timeSelectorSelectedContentColor
    } else {
        timeSelectorUnselectedContentColor
    }