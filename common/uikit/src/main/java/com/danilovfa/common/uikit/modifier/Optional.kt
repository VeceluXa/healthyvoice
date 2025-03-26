package com.danilovfa.common.uikit.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.optional(
    predicate: Boolean,
    block: @Composable Modifier.() -> Modifier
): Modifier =
    if (predicate) this.block() else this

@Composable
fun <T> Modifier.optional(
    value: T?,
    block: @Composable Modifier.(T) -> Modifier
): Modifier = if (value != null) this.block(value) else this