package com.danilovfa.common.uikit.composables.state

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.noRippleClickable
import com.danilovfa.common.uikit.preview.ThemePreviewParameter
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun Loader(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.primary,
    strokeWidth: Dp = 4.dp,
) {
    CircularProgressIndicator(
        color = color,
        strokeWidth = strokeWidth,
        modifier = modifier.size(40.dp),
    )
}

@Composable
fun LoaderStub(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    loaderBackgroundColor: Color = AppTheme.colors.background,
    text: String = ""
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .noRippleClickable { },
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = loaderBackgroundColor, shape = CircleShape),
        ) {
            Loader(modifier = Modifier.align(Alignment.Center))
        }

        if (text.isNotBlank()) {
            VSpacer(AppDimension.layoutMediumMargin)
            Text(
                text = text,
                style = AppTypography.bodyRegular16,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Suppress("ReusedModifierInstance")
fun LoaderLayout(
    modifier: Modifier = Modifier,
    showLoader: Boolean = false,
    backgroundColor: Color = AppTheme.colors.background,
    loaderBackgroundColor: Color = AppTheme.colors.background
) {
    AnimatedVisibility(
        visible = showLoader,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        LoaderStub(
            modifier = modifier,
            backgroundColor = backgroundColor,
            loaderBackgroundColor = loaderBackgroundColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoaderPreview(@PreviewParameter(ThemePreviewParameter::class) isDark: Boolean) {
    AppTheme(useDarkTheme = isDark) {
        LoaderLayout(showLoader = true)
    }
}