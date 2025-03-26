package com.danilovfa.common.uikit.composables.stub

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.button.ButtonLarge
import com.danilovfa.common.uikit.composables.button.OutlinedButtonLarge
import com.danilovfa.common.uikit.composables.state.Loader
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.optional
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun PagingItemErrorStub(
    message: String,
    buttonTitle: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = AppTypography.titleRegular22,
            color = AppTheme.colors.textSecondary,
        )
        Text(
            text = buttonTitle,
            style = AppTypography.bodyRegular16,
            color = AppTheme.colors.primary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(onClick = onButtonClick),
        )
    }
}

@Composable
fun PagingItemLoadingStub(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Loader(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun EmptyStub(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    buttonTitle: String? = null,
    onButtonClick: () -> Unit = {},
    fillMaxSize: Boolean = true,
) = BaseStub(
    modifier = modifier,
    title = title,
    message = message,
    image = image,
    buttonTitle = buttonTitle,
    onButtonClick = onButtonClick,
    fillMaxSize = fillMaxSize,
)

@Composable
fun ErrorStub(
    errorTitle: String,
    errorMessage: String,
    buttonTitle: String,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    fillMaxSize: Boolean = true,
) = BaseStub(
    modifier = modifier,
    title = errorTitle,
    message = errorMessage,
    image = image,
    buttonTitle = buttonTitle,
    onButtonClick = onRefreshClick,
    fillMaxSize = fillMaxSize,
)

@Composable
fun StandardErrorStub(onRefreshClick: () -> Unit, modifier: Modifier = Modifier) {
    ErrorStub(
        modifier = modifier,
        errorTitle = stringResource(strings.something_went_wrong),
        errorMessage = stringResource(strings.error_message),
        buttonTitle = stringResource(strings.refresh),
        onRefreshClick = onRefreshClick
    )
}

@Composable
fun SuccessStub(
    title: String,
    image: Painter,
    buttonTitle: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = image,
                contentDescription = null
            )
            VSpacer(size = 24.dp)
            Text(
                text = title,
                style = AppTypography.titleRegular22,
            )
            if (message != null) {
                Text(
                    text = message,
                    style = AppTypography.bodyRegular16,
                    modifier = Modifier.padding(top = 12.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
        ButtonLarge(
            text = buttonTitle,
            onClick = onButtonClick,
            modifier = Modifier.padding(AppDimension.layoutMainMargin),
        )
    }
}

@Composable
fun BaseStub(
    title: String,
    message: String,
    image: Painter?,
    buttonTitle: String?,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillMaxSize: Boolean = true,
) {
    Column(modifier = modifier) {
        val innerModifier = Modifier
            .padding(AppDimension.layoutMainMargin)
            .align(Alignment.CenterHorizontally)
            .optional(fillMaxSize) {
                this
                    .fillMaxSize()
                    .weight(1f)
            }
        Column(
            modifier = innerModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            image?.let {
                Image(
                    painter = image,
                    contentDescription = null
                )
            }
            Text(
                text = title,
                style = AppTypography.titleRegular22,
                modifier = Modifier.padding(top = 24.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = AppTypography.bodyRegular16,
                modifier = Modifier.padding(top = 12.dp),
                textAlign = TextAlign.Center,
            )
        }
        if (!buttonTitle.isNullOrBlank()) {
            OutlinedButtonLarge(
                text = buttonTitle,
                onClick = onButtonClick,
                modifier = Modifier.padding(AppDimension.layoutMainMargin),
            )
        }
    }
}
