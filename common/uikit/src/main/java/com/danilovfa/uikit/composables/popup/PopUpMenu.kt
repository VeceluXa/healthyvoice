package com.danilovfa.uikit.composables.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.danilovfa.core.library.text.Text
import com.danilovfa.core.library.text.get
import com.danilovfa.uikit.composables.HSpacer
import com.danilovfa.uikit.composables.state.Loader
import com.danilovfa.uikit.theme.AppDimension
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography

@Composable
fun PopupMenu(
    vararg menuItemsData: MenuItemsData,
    expandedState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    DropdownMenu(
        modifier = modifier.background(AppTheme.colors.backgroundPrimary),
        expanded = expandedState.value,
        onDismissRequest = { expandedState.value = false }
    ) {
        if (isLoading) {
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
                onClick = {}
            )
        }

        menuItemsData.forEach { item ->
            MenuItems(
                title = item.title.get(),
                icon = item.icon,
                tint = item.tint ?: AppTheme.colors.textPrimary,
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
    val onClick: () -> Unit
)

@Composable
private fun MenuItems(
    title: String,
    icon: Painter?,
    onClick: () -> Unit,
    tint: Color = AppTheme.colors.textPrimary
) {
    DropdownMenuItem(
        text = { GroupActionsItem(text = title, icon = icon, tint = tint) },
        onClick = onClick
    )
}

@Composable
private fun GroupActionsItem(
    text: String,
    icon: Painter?,
    tint: Color = AppTheme.colors.textPrimary,
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
                tint = tint
            )
            HSpacer(AppDimension.layoutMainMargin)
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            text = text,
            style = AppTypography.bodyRegular16,
            color = tint
        )
    }
}
