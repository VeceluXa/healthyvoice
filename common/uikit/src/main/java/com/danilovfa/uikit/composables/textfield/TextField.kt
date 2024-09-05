package com.danilovfa.uikit.composables.textfield

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField as MaterialOutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults.ContainerBox
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.contentPaddingWithLabel
import androidx.compose.material3.TextFieldDefaults.contentPaddingWithoutLabel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.resources.drawable.AppIcon
import com.danilovfa.uikit.composables.VSpacer
import com.danilovfa.uikit.composables.text.Text
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography
import com.danilovfa.uikit.theme.AppDimension

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTypography.bodyRegular14,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AppTextFieldDefaults.textFieldColors(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    TextField(
        value = textFieldValue,
        onValueChange = { fieldValue ->
            textFieldValueState = fieldValue
            if (value != fieldValue.text) {
                onValueChange(fieldValue.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        labelText = labelText,
        hintText = hintText,
        showHint = showHint,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = colors,
    )
}

@Suppress("LongParameterList")
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTypography.bodyRegular14,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AppTextFieldDefaults.outlinedTextFieldColors(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    MaterialOutlinedTextField(
        value = textFieldValue,
        onValueChange = { fieldValue: TextFieldValue ->
            textFieldValueState = fieldValue
            if (value != fieldValue.text) {
                onValueChange(fieldValue.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = colors,
        shape = RoundedCornerShape(5.dp)
    )
}

@Suppress("LongParameterList")
@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTypography.bodyRegular14,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AppTextFieldDefaults.textFieldColors(),
) {
    Column(modifier.padding(vertical = 8.dp)) {
        BaseTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = { Text(text = labelText) },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            colors = colors,
        )
        TextFieldHint(hintText, showHint, isError)
    }
}

@Suppress("LongParameterList")
@Composable
fun LargeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTypography.bodyRegular16,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    isRequired: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = AppTextFieldDefaults.textFieldColors(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    LargeTextField(
        value = textFieldValue,
        onValueChange = { fieldValue ->
            textFieldValueState = fieldValue
            if (value != fieldValue.text) {
                onValueChange(fieldValue.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        labelText = labelText,
        hintText = hintText,
        showHint = showHint,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        isRequired = isRequired,
        interactionSource = interactionSource,
        colors = colors,
    )
}

@Suppress("LongParameterList")
@Composable
fun LargeTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTypography.bodyRegular14,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    isRequired: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = AppTextFieldDefaults.textFieldColors(),
) {
    Column(modifier = modifier) {
        BaseTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
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
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            colors = colors,
        )
        TextFieldHint(hintText, showHint, isError)
    }
}

@Suppress("LongParameterList")
@Composable
fun LargeOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTypography.bodyRegular14,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AppTextFieldDefaults.outlinedTextFieldColors(),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        interactionSource = interactionSource,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = colors
    )
}

@Composable
internal fun ColumnScope.TextFieldHint(
    hintText: String = "",
    showHint: Boolean = true,
    isError: Boolean = false,
) {
    AnimatedVisibility(showHint) {
        Column {
            VSpacer(4.dp)
            val hintColor = if (isError) {
                AppTheme.colors.error
            } else {
                AppTheme.colors.buttonPrimaryDisabled
            }
            MaterialText(
                text = hintText,
                style = AppTypography.labelMedium11,
                color = hintColor,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BaseTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
) {
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    BasicTextField(
        value = value,
        modifier = modifier
            .background(colors.containerColor(enabled, isError, interactionSource).value, shape)
            .indicatorLine(enabled, isError, interactionSource, colors)
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = TextFieldDefaults.MinHeight
            ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError)),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.DecorationBox(
                value = value.text,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                isError = isError,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = null,
                suffix = null,
                supportingText = null,
                shape = TextFieldDefaults.shape,
                colors = colors,
                contentPadding = if (label == null) {
                    contentPaddingWithoutLabel()
                } else {
                    contentPaddingWithLabel()
                },
                container = {
                    ContainerBox(enabled, isError, interactionSource, colors, shape)
                },
            )
        }
    )
}

private fun Modifier.indicatorLine(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: TextFieldColors,
    horizontalPadding: Dp = AppDimension.layoutHorizontalMargin,
    focusedIndicatorLineThickness: Dp = TextFieldDefaults.FocusedIndicatorThickness,
    unfocusedIndicatorLineThickness: Dp = TextFieldDefaults.UnfocusedIndicatorThickness
) = composed {
    val stroke = animateBorderStrokeAsState(
        enabled = enabled,
        isError = isError,
        interactionSource = interactionSource,
        colors = colors,
        focusedBorderThickness = focusedIndicatorLineThickness,
        unfocusedBorderThickness = unfocusedIndicatorLineThickness
    )
    this.drawIndicatorLine(stroke.value, horizontalPadding)
}

@Composable
private fun animateBorderStrokeAsState(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: TextFieldColors,
    focusedBorderThickness: Dp,
    unfocusedBorderThickness: Dp
): State<BorderStroke> {
    val focused by interactionSource.collectIsFocusedAsState()
    val indicatorColor = colors.indicatorColor(enabled, isError, interactionSource)
    val targetThickness = if (focused) focusedBorderThickness else unfocusedBorderThickness
    val animatedThickness = if (enabled) {
        animateDpAsState(targetThickness, tween(durationMillis = 150), label = "borderStrokeAnimation")
    } else {
        rememberUpdatedState(unfocusedBorderThickness)
    }
    return rememberUpdatedState(
        BorderStroke(animatedThickness.value, SolidColor(indicatorColor.value))
    )
}

internal fun Modifier.drawIndicatorLine(
    indicatorBorder: BorderStroke,
    horizontalPadding: Dp,
): Modifier {
    val strokeWidthDp = indicatorBorder.width
    return drawWithContent {
        drawContent()
        if (strokeWidthDp == Dp.Hairline) return@drawWithContent
        val strokeWidth = strokeWidthDp.value * density
        val y = size.height - strokeWidth / 2
        drawLine(
            indicatorBorder.brush,
            Offset(horizontalPadding.toPx(), y),
            Offset(size.width - horizontalPadding.toPx(), y),
            strokeWidth
        )
    }
}

@Composable
fun String.addAsterisk(): AnnotatedString = buildAnnotatedString {
    append(this@addAsterisk)
    withStyle(style = SpanStyle(color = AppTheme.colors.error)) {
        append("\u002A")
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("StringLiteralDuplication")
private fun TextFieldPreview() {
    AppTheme {
        Column(
            Modifier
                .padding(8.dp)
                .background(AppTheme.colors.backgroundPrimary)
        ) {
            LargeTextField(
                value = "With label",
                onValueChange = {},
                labelText = "Text Field",
                trailingIcon = { Icon(AppIcon.Clear, "Clear") },
            )
            LargeTextField(
                value = "",
                onValueChange = {},
                labelText = "Text Field",
                trailingIcon = { Icon(AppIcon.Clear, "Clear") },
            )
            LargeTextField(
                value = "With label",
                onValueChange = {},
                labelText = "Text Field",
                hintText = "Hint Text",
                isError = true,
                trailingIcon = { Icon(AppIcon.Clear, "Clear") },
            )
        }
    }
}
