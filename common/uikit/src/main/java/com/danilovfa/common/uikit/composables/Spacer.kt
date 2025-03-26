package com.danilovfa.common.uikit.composables

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Horizontal spacer
 * @param size Width of the spacer
 */
@Composable
fun HSpacer(size: Dp, modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.width(size))

/**
 * Vertical spacer
 * @param size Height of the spacer
 */
@Composable
fun VSpacer(size: Dp, modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.height(size))

/**
 * Weight spacer that takes up all remaining height
 */
@Composable
fun ColumnScope.WSpacer(modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.weight(1f))

/**
 * Weight spacer that takes up all remaining width
 */
@Composable
fun RowScope.WSpacer(modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.weight(1f))