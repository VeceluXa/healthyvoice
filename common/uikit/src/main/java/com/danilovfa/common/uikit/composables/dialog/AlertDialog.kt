package com.danilovfa.common.uikit.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.danilovfa.common.base.dialog.AlertDialogState
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.core.presentation.get
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.animation.AnimatedVisibilityNullableValue
import com.danilovfa.common.uikit.composables.button.AppButtonColors
import com.danilovfa.common.uikit.composables.button.ButtonLarge
import com.danilovfa.common.uikit.composables.button.OutlinedButton
import com.danilovfa.common.uikit.composables.button.TextButtonLarge
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.composables.textfield.AppTextFieldDefaults
import com.danilovfa.common.uikit.composables.textfield.TextFieldOutlinedLarge
import com.danilovfa.common.uikit.modifier.noRippleClickable
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun AlertDialog(
    alertDialogState: AlertDialogState?,
    customDialog: @Composable ((params: Any) -> Unit)? = null
) {
    AnimatedVisibilityNullableValue(alertDialogState) { state ->
        when (state) {
            is AlertDialogState.DefaultDialogState -> DefaultAlertDialog(
                title = state.title,
                text = state.text,
                illustration = state.illustration?.invoke(),
                confirmButtonTitle = state.confirmButtonTitle,
                dismissButtonTitle = state.dismissButtonTitle,
                onConfirmClick = state.onConfirmClick,
                isConfirmNegative = state.isConfirmNegative,
                onDismissClick = state.onDismissClick
            )

            is AlertDialogState.ImageDialogState -> ImageAlertDialog(
                title = state.titleText,
                image = state.image(),
                onDismissClick = state.onDismissClick,
                onConfirmClick = state.onConfirmClick,
                confirmButtonTitle = state.confirmButtonTitle
            )

            is AlertDialogState.TextOnlyDialogState -> TextAlertDialog(
                text = state.text,
                onDismissClick = state.onDismissClick,
                confirmButtonTitle = state.confirmButtonTitle,
                dismissButtonTitle = state.dismissButtonTitle,
                onConfirmClick = state.onConfirmClick,
                isRequired = state.isRequired
            )

            is AlertDialogState.TextFieldDialogState -> TextFieldAlertDialog(
                title = state.title,
                hint = state.hint,
                initialText = state.initialText,
                onDismissClick = state.onDismissClick,
                onConfirmClick = { state.onApplyClick(it) },
                confirmButtonText = state.confirmButtonText,
                dismissButtonText = state.dismissButtonText,
                keyboardOptions = state.keyboardOptions,
                validateText = state.validateText,
                validateConfirm = state.validateConfirm
            )

            is AlertDialogState.Custom -> {
                customDialog?.let {
                    CustomDialog(
                        content = { customDialog(state.data) },
                        onDismissClick = { state.onDismissClick }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomDialog(
    content: @Composable () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        onDismissRequest = onDismissClick,
        modifier = modifier,
        content = content
    )
}

@Composable
private fun TextAlertDialog(
    text: Text,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonTitle: Text? = null,
    dismissButtonTitle: Text? = null,
    onConfirmClick: (() -> Unit)? = null,
    isRequired: Boolean = false,
) {
    val context = LocalContext.current

    BaseDialog(
        onDismissRequest = { if (isRequired.not()) onDismissClick() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = text.get(context),
                style = AppTypography.bodyRegular16,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (confirmButtonTitle != null || dismissButtonTitle != null) {
                VSpacer(16.dp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                dismissButtonTitle?.let {
                    OutlinedButton(
                        text = dismissButtonTitle.get(context),
                        onClick = onDismissClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                    )

                    HSpacer(16.dp)
                }

                confirmButtonTitle?.let {
                    OutlinedButton(
                        text = confirmButtonTitle.get(context),
                        onClick = { onConfirmClick?.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageAlertDialog(
    title: Text,
    image: Painter,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonTitle: Text? = null,
    onConfirmClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    BaseDialog(
        onDismissRequest = onDismissClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Text(
                    text = title.get(context),
                    style = AppTypography.titleRegular22,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                )

                IconButton(
                    onClick = onDismissClick
                ) {
                    Icon(
                        painter = AppIcon.Close,
                        tint = AppTheme.colors.primary,
                        contentDescription = "Close"
                    )
                }
            }

            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
            )

            confirmButtonTitle?.let {
                ButtonLarge(
                    text = confirmButtonTitle.get(context),
                    onClick = { onConfirmClick?.invoke() },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DefaultAlertDialog(
    text: Text,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: Text? = null,
    illustration: Painter? = null,
    confirmButtonTitle: Text? = null,
    isConfirmNegative: Boolean = false,
    dismissButtonTitle: Text? = null,
    onConfirmClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    BaseDialog(
        onDismissRequest = onDismissClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            illustration?.let { painter ->
                Image(
                    painter = painter,
                    contentDescription = null
                )
                VSpacer(8.dp)
            }

            title?.get(context).takeUnless { it == "" }?.let { title ->
                Text(
                    text = title,
                    style = AppTypography.titleRegular22,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                VSpacer(8.dp)
            }

            Text(
                text = text.get(context),
                style = AppTypography.bodyRegular16,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )

            VSpacer(16.dp)

            dismissButtonTitle?.let {
                TextButtonLarge(
                    text = dismissButtonTitle.get(context),
                    onClick = onDismissClick
                )
                VSpacer(4.dp)
            }

            confirmButtonTitle?.let {
                ButtonLarge(
                    text = confirmButtonTitle.get(context),
                    colors = if (isConfirmNegative) AppButtonColors.errorButtonColors() else AppButtonColors.primaryButtonColors(),
                    onClick = { onConfirmClick?.invoke() }
                )
            }
        }
    }
}

@Composable
private fun TextFieldAlertDialog(
    title: Text?,
    initialText: Text?,
    hint: Text?,
    onDismissClick: () -> Unit,
    onConfirmClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    validateText: (String) -> Boolean = { true },
    validateConfirm: (String) -> Boolean = { true },
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    confirmButtonText: Text = Text.Resource(strings.apply),
    dismissButtonText: Text = Text.Resource(strings.close)
) {
    val context = LocalContext.current
    var textFieldValue by remember { mutableStateOf(initialText?.get(context) ?: "") }

    BaseDialog(
        onDismissRequest = onDismissClick,
    ) {
        Column(
            modifier = modifier
                .imePadding()
                .fillMaxWidth()
                .background(AppTheme.colors.background)
                .padding(AppDimension.layoutMainMargin)
        ) {
            title?.let {
                Text(
                    text = title.get().uppercase(),
                    style = AppTypography.labelMedium12.copy(
                        letterSpacing = 5.sp
                    ),
                )
            }
            VSpacer(8.dp)
            TextFieldOutlinedLarge(
                value = textFieldValue,
                onValueChange = {
                    if (validateText(it)) {
                        textFieldValue = it
                    }
                },
                textStyle = AppTypography.bodyRegular16,
                colors = AppTextFieldDefaults.outlinedTextFieldColors(),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                labelText = hint?.get() ?: "",
            )
            VSpacer(AppDimension.layoutMediumMargin)

            ButtonLarge(
                text = confirmButtonText.get(),
                onClick = { onConfirmClick(textFieldValue) },
                enabled = validateConfirm(textFieldValue)
            )
            VSpacer(AppDimension.layoutSmallMargin)
            TextButtonLarge(
                text = dismissButtonText.get(),
                onClick = onDismissClick
            )
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp)
//            ) {
//                OutlinedButton(
//                    text = dismissButtonText.get(),
//                    onClick = onDismissClick,
//                    textStyle = AppTypography.bodyRegular14,
//                    contentPadding = SmallButtonContentPadding,
//                    icon = AppIcon.Close
//                )
//                WSpacer()
//                FloatTextButton(
//                    text = confirmButtonText.get(),
//                    onClick = {
//                        onDismissClick()
//                        onConfirmClick(textFieldValue)
//                    },
//                    textStyle = AppTypography.bodyRegular14,
//                    icon = AppIcon.Check,
//                    enabled = validateConfirm(textFieldValue)
//                )
//
//            }
        }
    }
}

@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundColor: Color = AppTheme.colors.background,
    contentColor: Color = AppTheme.colors.onBackground,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp),
    alignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable(onDismissRequest),
            contentAlignment = alignment
        ) {
            Surface(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                shape = shape,
                color = backgroundColor,
                contentColor = contentColor
            ) {
                content()
            }
        }
    }
}
