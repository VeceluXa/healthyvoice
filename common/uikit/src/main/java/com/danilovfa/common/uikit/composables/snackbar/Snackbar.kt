package com.danilovfa.common.uikit.composables.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTypography
import kotlin.math.max

@Composable
internal fun Snackbar(
    snackbarData: StyledSnackbarData,
    modifier: Modifier = Modifier,
    elevation: Dp = 6.dp,
) {
    Snackbar(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppDimension.layoutMainMargin),
        content = {
            SnackbarTextOnly(
                text = snackbarData.message,
                style = snackbarData.style,
                modifier = modifier,
            )
        },
        shape = snackbarData.style.shape,
        backgroundColor = snackbarData.style.backgroundColor,
        contentColor = snackbarData.style.contentColor,
        elevation = elevation
    )
}

@Composable
private fun SnackbarTextOnly(
    text: String,
    style: SnackbarStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .heightIn(SnackbarMinHeightSingleLine)
            .background(style.backgroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            overflow = TextOverflow.Ellipsis,
            style = AppTypography.bodyRegular16,
            color = style.contentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = if (style.icon != null) 32.dp else 0.dp)
        )

        style.icon?.let { icon ->
            HSpacer(12.dp)
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun Snackbar(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = SnackbarDefaults.color,
    contentColor: Color = Color.White,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        shadowElevation = elevation,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            val textStyle = MaterialTheme.typography.bodyMedium
            ProvideTextStyle(value = textStyle) {
                TextOnlySnackbar(content)
            }
        }
    }
}

/**
 * Slightly changed version of original Compose Snackbar's.
 * Text placed in the center of the view instead of start.
 */
@Composable
private fun TextOnlySnackbar(content: @Composable BoxScope.() -> Unit) {
    Layout({
        Box(
            modifier = Modifier.padding(
                horizontal = HorizontalSpacing,
                vertical = SnackbarVerticalPadding
            ),
            content = content,
        )
    }) { measurables, constraints ->
        require(measurables.size == 1) {
            "text for Snackbar expected to have exactly only one child"
        }
        val textPlaceable = measurables.first().measure(constraints)
        val firstBaseline = textPlaceable[FirstBaseline]
        val lastBaseline = textPlaceable[LastBaseline]
        require(firstBaseline != AlignmentLine.Unspecified) { "No baselines for text" }
        require(lastBaseline != AlignmentLine.Unspecified) { "No baselines for text" }

        val singleLine = firstBaseline == lastBaseline
        val minHeight = if (singleLine) SnackbarMinHeightSingleLine else SnackbarMinHeightWithImage
        val minWidth = if (singleLine) SnackbarMinWidth else constraints.maxWidth.dp

        val containerHeight = max(minHeight.roundToPx(), textPlaceable.height)
        val containerWidth = max(minWidth.roundToPx(), textPlaceable.width)

        layout(containerWidth, containerHeight) {
            val textPlaceY = (containerHeight - textPlaceable.height) / 2
            val textPlaceX = (containerWidth - textPlaceable.width) / 2
            textPlaceable.placeRelative(textPlaceX, textPlaceY)
        }
    }
}

private val HorizontalSpacing = 12.dp
private val SnackbarVerticalPadding = 10.dp
private val SnackbarMinHeightSingleLine = 44.dp
private val SnackbarMinHeightWithImage = 68.dp
private val SnackbarMinWidth = 64.dp
