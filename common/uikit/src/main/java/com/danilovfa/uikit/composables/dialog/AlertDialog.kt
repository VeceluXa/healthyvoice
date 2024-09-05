package com.danilovfa.uikit.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.danilovfa.core.library.dialog.AlertDialogState
import com.danilovfa.resources.R
import com.danilovfa.core.library.text.Text
import com.danilovfa.resources.drawable.AppIcon
import com.danilovfa.uikit.composables.HSpacer
import com.danilovfa.uikit.composables.VSpacer
import com.danilovfa.uikit.composables.WSpacer
import com.danilovfa.uikit.composables.animation.AnimatedVisibilityNullableValue
import com.danilovfa.uikit.composables.button.FloatTextButton
import com.danilovfa.uikit.composables.button.OutlinedButton
import com.danilovfa.uikit.composables.button.PrimaryButtonLarge
import com.danilovfa.uikit.composables.button.SmallButtonContentPadding
import com.danilovfa.uikit.composables.button.TextButtonLarge
import com.danilovfa.uikit.composables.textfield.AppTextFieldDefaults
import com.danilovfa.uikit.composables.textfield.LargeOutlinedTextField
import com.danilovfa.uikit.composables.text.Text
import com.danilovfa.uikit.modifier.noRippleClickable
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography

@Composable
fun AlertDialog(
    alertDialogState: AlertDialogState?,
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
                onApplyClick = { state.onApplyClick(it) },
            )
        }
    }
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
                        tint = AppTheme.colors.buttonPrimary,
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
                PrimaryButtonLarge(
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

            confirmButtonTitle?.let {
                PrimaryButtonLarge(
                    text = confirmButtonTitle.get(context),
                    onClick = { onConfirmClick?.invoke() }
                )
                VSpacer(4.dp)
            }

            dismissButtonTitle?.let {
                TextButtonLarge(
                    text = dismissButtonTitle.get(context),
                    onClick = onDismissClick
                )
            }
        }
    }
}

@Composable
private fun TextFieldAlertDialog(
    title: Text,
    initialText: Text,
    hint: Text,
    onDismissClick: () -> Unit,
    onApplyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var textFieldValue by remember { mutableStateOf(initialText.get(context)) }

    BaseDialog(
        onDismissRequest = onDismissClick,
        alignment = Alignment.BottomCenter,
        paddingValues = PaddingValues(),
        shape = RectangleShape
    ) {
        Column(
            modifier = modifier
                .imePadding()
                .systemBarsPadding()
                .fillMaxWidth()
                .background(AppTheme.colors.backgroundPrimary)
                .padding(16.dp)
        ) {
            Text(
                text = title.get(context).uppercase(),
                style = AppTypography.labelMedium12.copy(
                    letterSpacing = 5.sp
                ),
            )
            VSpacer(8.dp)
            LargeOutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
//            hintText = stringResource(R.string.character_builder_enter_description),
                textStyle = AppTypography.bodyRegular16,
                colors = AppTextFieldDefaults.outlinedTextFieldColors(),
                singleLine = false,
                modifier = Modifier.heightIn(104.dp)
            )
            VSpacer(8.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                OutlinedButton(
                    text = stringResource(R.string.close),
                    onClick = onDismissClick,
                    textStyle = AppTypography.bodyRegular14,
                    contentPadding = SmallButtonContentPadding,
                    icon = AppIcon.Close
                )

                WSpacer()

                FloatTextButton(
                    text = stringResource(R.string.apply),
                    onClick = {
                        onDismissClick()
                        onApplyClick(textFieldValue)
                    },
                    textStyle = AppTypography.bodyRegular14,
                    icon = AppIcon.Check,
                )
            }
        }
    }
}

@Composable
private fun BaseDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundColor: Color = AppTheme.colors.backgroundPrimary,
    contentColor: Color = AppTheme.colors.backgroundOnPrimary,
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
