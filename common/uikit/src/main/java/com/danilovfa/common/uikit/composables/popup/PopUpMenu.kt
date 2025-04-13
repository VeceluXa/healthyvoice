package com.danilovfa.common.uikit.composables.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.core.presentation.get
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.state.Loader
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun PopupMenu(
    vararg menuItemsData: MenuItemsData,
    expandedState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    DropdownMenu(
        containerColor = AppTheme.colors.surface,
        modifier = modifier.background(AppTheme.colors.surface),
        expanded = expandedState.value,
        onDismissRequest = { expandedState.value = false }
    ) {
        if (isLoading) {
            MenuItemLoader()
        }

        menuItemsData.forEach { item ->
            MenuItems(
                title = item.title.get(),
                icon = item.icon,
                enabled = item.enabled,
                iconTint = item.tint,
                onClick = {
                    item.onClick.invoke()
                    expandedState.value = false
                },
            )
        }
    }
}

data class MenuItem<T>(
    val title: Text,
    val item: T
)

data class MenuItemsData(
    val title: Text,
    val tint: Color? = null,
    val icon: Painter? = null,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)

@Composable
internal fun MenuItemLoader(
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Loader(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        },
        onClick = {},
        modifier = modifier
    )
}

@Composable
internal fun MenuItems(
    title: String,
    icon: Painter?,
    onClick: () -> Unit,
    iconTint: Color? = null,
    enabled: Boolean = true
) {
    DropdownMenuItem(
        text = {
            GroupActionsItem(
                text = title,
                icon = icon,
                tint = iconTint,
                enabled = enabled
            )
        },
        enabled = enabled,
        onClick = onClick
    )
}

@Composable
private fun GroupActionsItem(
    text: String,
    icon: Painter?,
    enabled: Boolean = true,
    tint: Color? = null
) {
    Row(
        modifier = Modifier.padding(end = AppDimension.layoutMediumMargin)
    ) {
        icon?.let {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(24.dp),
                painter = icon,
                contentDescription = null,
                tint = tint ?: if (enabled) AppTheme.colors.primary else AppTheme.colors.primaryDisabled
            )
            HSpacer(AppDimension.layoutMainMargin)
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            text = text,
            style = AppTypography.bodyRegular16,
            color = if (enabled) AppTheme.colors.textPrimary else AppTheme.colors.textDisabled
        )
    }
}
