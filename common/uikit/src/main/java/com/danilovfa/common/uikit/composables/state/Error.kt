package com.danilovfa.common.uikit.composables.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.core.presentation.get
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.button.TextButton
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.noRippleClickable
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@Composable
fun ErrorStub(
    modifier: Modifier = Modifier,
    title: Text = Text.Resource(strings.error),
    description: Text = Text.Resource(strings.something_went_wrong),
    buttonText: Text = Text.Resource(strings.refresh),
    onButtonClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .noRippleClickable { }
    ) {
        Text(
            text = title.get(),
            style = AppTypography.titleMedium22,
            color = AppTheme.colors.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        VSpacer(AppDimension.layoutMainMargin)
        Text(
            text = description.get(),
            style = AppTypography.bodyRegular16,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        VSpacer(AppDimension.layoutMainMargin)
        TextButton(
            text = buttonText.get(),
            onClick = onButtonClick
        )
    }
}