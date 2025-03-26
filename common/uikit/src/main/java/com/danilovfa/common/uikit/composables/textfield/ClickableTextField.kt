package com.danilovfa.common.uikit.composables.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun LargeSelectableTextField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = false,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    isRequired: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AppTextFieldDefaults.textFieldColors(),
) {
    val textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    Column(modifier = modifier) {
        BaseTextField(
            value = textFieldValue,
            onValueChange = {},
            enabled = false,
            readOnly = true,
            textStyle = textStyle,
            label = {
                if (isRequired) {
                    Text(text = labelText.addAsterisk())
                } else {
                    Text(text = labelText)
                }
            },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            maxLines = maxLines,
            colors = colors,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        )
        TextFieldHint(hintText, showHint, isError)
    }
}

@Composable
fun SelectableTextField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = false,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    isRequired: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AppTextFieldDefaults.textFieldColors(),
) {
    val textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    Column(modifier = modifier) {
        BaseTextField(
            value = textFieldValue,
            onValueChange = {},
            enabled = false,
            readOnly = true,
            textStyle = textStyle,
            label = {
                if (isRequired) {
                    Text(text = labelText.addAsterisk())
                } else {
                    Text(text = labelText)
                }
            },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            maxLines = maxLines,
            colors = colors,
            modifier = Modifier
                .clickable(onClick = onClick),
        )
        TextFieldHint(hintText, showHint, isError)
    }
}