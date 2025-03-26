package com.danilovfa.common.uikit.composables.icon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.optional
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun TextIcon(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AppTypography.bodyMedium16,
    shape: Shape = CircleShape,
    tint: Color = AppTheme.colors.primary,
    iconBorder: BorderStroke? = BorderStroke(
        width = 1.dp,
        color = tint
    ),
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .optional(iconBorder != null) {
                iconBorder?.let {
                    border(
                        border = iconBorder,
                        shape = shape
                    )
                } ?: this
            }
    ) {
        Text(
            text = text.trim(),
            style = textStyle,
            color = tint,
            modifier = Modifier
                .padding(
                    horizontal = AppDimension.layoutMediumMargin,
                    vertical = AppDimension.layoutSmallMargin
                )
        )
    }
}