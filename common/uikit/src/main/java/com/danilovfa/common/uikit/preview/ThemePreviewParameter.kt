package com.danilovfa.common.uikit.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ThemePreviewParameter : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true)
}