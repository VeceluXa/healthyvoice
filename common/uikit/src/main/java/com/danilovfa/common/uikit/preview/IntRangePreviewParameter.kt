package com.danilovfa.common.uikit.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

open class IntRangePreviewParameter(startIndex: Int, endIndex: Int) :
    PreviewParameterProvider<Int> {
    override val values: Sequence<Int> = (startIndex..endIndex).toList().asSequence()
}