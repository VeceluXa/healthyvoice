package com.danilovfa.common.uikit.composables.toolbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.uikit.composables.animation.AnimatedVisibilityNullableValue
import com.danilovfa.common.uikit.composables.animation.IconAnimatedVisibility
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.surface
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: AppToolbarColors = AppToolbarColors.primaryToolbarColors(),
    enableShadow: Boolean = false
) {
    Toolbar(
        title = title,
        navigationIcon = null,
        onNavigationClick = {},
        modifier = modifier,
        colors = colors,
        enableShadow = enableShadow
    )
}

@Composable
fun Toolbar(
    navigationIcon: NavigationIcon?,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: AppToolbarColors = AppToolbarColors.primaryToolbarColors(),
    title: String = "",
    enableShadow: Boolean = false
) {
    val elevation = if (enableShadow) 6.dp else 0.dp
    val bottomPadding = if (enableShadow) 2.dp else 0.dp
    Toolbar(
        modifier = modifier
            .padding(bottom = bottomPadding)
            .shadow(elevation),
        color = colors.backgroundColor,
    ) {
        Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            NavigationIcon(
                navigationIcon = navigationIcon,
                onNavigationClick = onNavigationClick,
                colors = colors.toIconButtonColors()
            )

            Column(
                Modifier
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
                    .weight(1f),
            ) {
                Text(text = title, style = AppTypography.titleMedium18)
            }
        }
    }
}

@Composable
fun Toolbar(
    navigationIcon: NavigationIcon?,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    colors: AppToolbarColors = AppToolbarColors.primaryToolbarColors(),
    contentBelowToolbar: @Composable (ColumnScope.() -> Unit)? = null,
    title: String = "",
    enableShadow: Boolean = false,
    isActionsVisible: Boolean = true,
) {
    val elevation = if (enableShadow) 6.dp else 0.dp
    val bottomPadding = if (enableShadow) 2.dp else 0.dp
    val titleHorizontalPadding = if (navigationIcon != null) AppDimension.layoutMediumMargin else AppDimension.layoutHorizontalMargin

    Column {
        Toolbar(
            modifier = modifier
                .padding(bottom = bottomPadding)
                .shadow(elevation = elevation),
            color = colors.backgroundColor
        ) {
            Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                NavigationIcon(
                    navigationIcon = navigationIcon,
                    onNavigationClick = onNavigationClick,
                    colors = colors.toIconButtonColors()
                )

                Column(
                    Modifier
                        .padding(horizontal = titleHorizontalPadding)
                        .weight(1f),
                ) {
                    Text(text = title, style = AppTypography.titleMedium18)
                }

                IconAnimatedVisibility(visible = isActionsVisible) {
                    Row(
                        modifier = Modifier.padding(end = IconCornerPadding),
                        content = actions,
                    )
                }
            }
        }

        contentBelowToolbar?.invoke(this)
    }
}

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.surface,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .surface(
                backgroundColor = color,
                shape = RectangleShape,
                elevation = 0.dp
            )
            .padding(contentPadding)
            .fillMaxWidth()
            .height(AppBarHeight)
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.CenterStart,
        content = content,
    )
}

@Composable
private fun NavigationIcon(
    navigationIcon: NavigationIcon?,
    colors: IconButtonColors,
    onNavigationClick: () -> Unit,
) {
    AnimatedVisibilityNullableValue(navigationIcon) { icon ->
        IconButton(
            onClick = onNavigationClick,
            colors = colors,
            modifier = Modifier
                .padding(start = IconCornerPadding - icon.innerPadding),
            enabled = navigationIcon != null,
            content = { icon() },
        )
    }
}


val AppBarHeight: Dp = 48.dp
private val IconCornerPadding = AppDimension.toolbarHorizontalMargin - (AppDimension.minTouchSize - 32.dp) / 2
private val ContentCornerPadding = AppDimension.toolbarHorizontalMargin + AppDimension.minTouchSize

enum class NavigationIcon(
    private val painter: @Composable () -> Painter,
    val innerPadding: Dp,
) {
    Back(
        painter = { AppIcon.ArrowBack },
        innerPadding = 8.dp,
    ),

    Close(
        painter = { AppIcon.Close },
        innerPadding = 8.dp,
    ),

    Settings(
        painter = { AppIcon.Settings },
        innerPadding = 8.dp,
    );

    @Composable
    operator fun invoke() {
        Icon(
            painter = painter(),
            contentDescription = null,
            tint = AppTheme.colors.textPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}