package com.danilovfa.uikit.composables.toolbar

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.uikit.composables.animation.AnimatedVisibilityNullableValue
import com.danilovfa.uikit.composables.animation.IconAnimatedVisibility
import com.danilovfa.uikit.composables.text.Text
import com.danilovfa.uikit.modifier.surface
import com.danilovfa.uikit.theme.AppDimension
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    startIcon: @Composable (() -> Unit)? = null,
    enableShadow: Boolean = false
) {
    Toolbar(
        title = title,
        navigationIcon = null,
        onNavigationClick = {},
        startIcon = startIcon,
        modifier = modifier,
        enableShadow = enableShadow
    )
}

@Composable
fun Toolbar(
    navigationIcon: NavigationIcon?,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    startIcon: (@Composable () -> Unit)? = null,
    color: Color = AppTheme.colors.backgroundPrimary,
    title: String = "",
    enableShadow: Boolean = false
) {
    val elevation = if (enableShadow) 6.dp else 0.dp
    val bottomPadding = if (enableShadow) 2.dp else 0.dp
    Toolbar(
        modifier = modifier
            .padding(bottom = bottomPadding)
            .shadow(elevation),
        color = color,
    ) {
        Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            NavigationIcon(navigationIcon, onNavigationClick)

            startIcon?.invoke()

            Column(
                Modifier
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
                    .weight(1f),
            ) {
                Text(text = title, style = AppTypography.titleMedium22)
            }
        }
    }
}

@Composable
fun Toolbar(
    navigationIcon: NavigationIcon?,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.backgroundPrimary,
    actions: @Composable RowScope.() -> Unit = {},
    contentBelowToolbar: @Composable (ColumnScope.() -> Unit)? = null,
    startIcon: (@Composable () -> Unit)? = null,
    title: String = "",
    enableShadow: Boolean = true,
    isActionsVisible: Boolean = true,
) {
    val elevation = if (enableShadow) 6.dp else 0.dp
    val bottomPadding = if (enableShadow) 2.dp else 0.dp

    Column {
        Toolbar(
            modifier = modifier
                .padding(bottom = bottomPadding)
                .shadow(elevation = elevation),
            color = color
        ) {
            Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                NavigationIcon(navigationIcon, onNavigationClick)

                startIcon?.invoke()

                Column(
                    Modifier
                        .padding(horizontal = AppDimension.layoutHorizontalMargin)
                        .weight(1f),
                ) {
                    Text(text = title, style = AppTypography.titleMedium22)
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
    color: Color = AppTheme.colors.backgroundPrimary,
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
    onNavigationClick: () -> Unit,
) {
    AnimatedVisibilityNullableValue(navigationIcon) { icon ->
        IconButton(
            onClick = onNavigationClick,
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
        painter = { rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack) },
        innerPadding = 8.dp,
    ),

    Close(
        painter = { rememberVectorPainter(Icons.Filled.Close) },
        innerPadding = 8.dp,
    ),

    Settings(
        painter = { rememberVectorPainter(Icons.Filled.Settings) },
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

@Immutable
data class AppToolbarColors(
    val backgroundColor: Color,
    val contentColor: Color,
    val buttonColor: Color,
    val buttonDisabledColor: Color
) {
    @Composable
    fun buttonColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) buttonColor else buttonDisabledColor)
    }

    companion object {
        @Composable
        fun primaryToolbarColors(
            backgroundColor: Color = AppTheme.colors.backgroundPrimary,
            contentColor: Color = AppTheme.colors.backgroundOnPrimary,
            buttonColor: Color = AppTheme.colors.buttonPrimary,
            buttonDisabledColor: Color = AppTheme.colors.buttonPrimaryDisabled
        ): AppToolbarColors = AppToolbarColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            buttonColor = buttonColor,
            buttonDisabledColor = buttonDisabledColor,
        )

        @Composable
        fun secondaryToolbarColors(
            backgroundColor: Color = AppTheme.colors.backgroundSecondary,
            contentColor: Color = AppTheme.colors.backgroundOnSecondary,
            buttonColor: Color = AppTheme.colors.buttonPrimary,
            buttonDisabledColor: Color = AppTheme.colors.buttonPrimaryDisabled
        ): AppToolbarColors = AppToolbarColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            buttonColor = buttonColor,
            buttonDisabledColor = buttonDisabledColor,
        )

        @Composable
        fun tertiaryToolbarColors(
            backgroundColor: Color = AppTheme.colors.backgroundTertiary,
            contentColor: Color = AppTheme.colors.backgroundOnTertiary,
            buttonColor: Color = AppTheme.colors.backgroundOnTertiary,
            buttonDisabledColor: Color = AppTheme.colors.buttonPrimaryDisabled
        ): AppToolbarColors = AppToolbarColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            buttonColor = buttonColor,
            buttonDisabledColor = buttonDisabledColor,
        )
    }
}

