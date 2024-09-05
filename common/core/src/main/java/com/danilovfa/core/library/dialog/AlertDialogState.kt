package com.danilovfa.core.library.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.danilovfa.core.library.text.Text

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
        val onDismissClick: () -> Unit
    ) : AlertDialogState

    data class TextFieldDialogState(
        val title: Text,
        val hint: Text,
        val initialText: Text,
        val onApplyClick: (String) -> Unit,
        val onDismissClick: () -> Unit
    ) : AlertDialogState
}