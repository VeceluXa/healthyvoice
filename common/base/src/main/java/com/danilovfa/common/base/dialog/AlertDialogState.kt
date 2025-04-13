package com.danilovfa.common.base.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.danilovfa.common.core.presentation.Text

sealed interface AlertDialogState {
    data class ImageDialogState(
        val titleText: Text,
        val image: @Composable () -> Painter,
        val onDismissClick: () -> Unit,
        val confirmButtonTitle: Text? = null,
        val onConfirmClick: (() -> Unit)? = null
    ) : AlertDialogState

    data class TextOnlyDialogState(
        val text: Text,
        val onDismissClick: () -> Unit,
        val confirmButtonTitle: Text? = null,
        val dismissButtonTitle: Text? = null,
        val onConfirmClick: (() -> Unit)? = null,
        val isRequired: Boolean = false
    ) : AlertDialogState

    data class DefaultDialogState(
        val text: Text,
        val title: Text? = null,
        val illustration: @Composable (() -> Painter)? = null,
        val confirmButtonTitle: Text? = null,
        val dismissButtonTitle: Text? = null,
        val onConfirmClick: (() -> Unit)? = null,
        val isConfirmNegative: Boolean = false,
        val onDismissClick: () -> Unit
    ) : AlertDialogState

    data class TextFieldDialogState(
        val onApplyClick: (String) -> Unit,
        val onDismissClick: () -> Unit,
        val confirmButtonText: Text,
        val dismissButtonText: Text,
        val initialText: Text? = null,
        val keyboardOptions: KeyboardOptions = KeyboardOptions(),
        val validateText: (String) -> Boolean = { true },
        val validateConfirm: (String) -> Boolean = { true },
        val hint: Text? = null,
        val title: Text? = null
    ) : AlertDialogState

    data class Custom(
        val data: Any,
        val onDismissClick: () -> Unit
    ) : AlertDialogState
}