package com.danilovfa.common.uikit.composables.navigation_item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.surfaceSection
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun NavigationItem(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .surfaceSection(
                backgroundColor = if (enabled) AppTheme.colors.surface else AppTheme.colors.surfaceDisabled,
                shape = shape,
                onClick = if (enabled) onClick else null
            )
            .padding(AppDimension.layoutMainMargin)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = AppTheme.colors.textSecondary
        )

        HSpacer(AppDimension.layoutMediumMargin)

        Text(
            text = text,
            style = AppTypography.bodyRegular16,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}