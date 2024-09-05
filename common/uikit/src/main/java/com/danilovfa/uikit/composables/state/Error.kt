package com.danilovfa.uikit.composables.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.danilovfa.core.library.text.Text
import com.danilovfa.core.library.text.get
import com.danilovfa.resources.R
import com.danilovfa.uikit.composables.VSpacer
import com.danilovfa.uikit.composables.button.TextButton
import com.danilovfa.uikit.composables.text.Text as TextComposable
import com.danilovfa.uikit.modifier.noRippleClickable
import com.danilovfa.uikit.theme.AppDimension
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography

@Composable
fun ErrorStub(
    modifier: Modifier = Modifier,
    title: Text = Text.Resource(R.string.error),
    description: Text = Text.Resource(R.string.something_went_wrong),
    buttonText: Text = Text.Resource(R.string.refresh),
    onButtonClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .noRippleClickable { }
    ) {
        TextComposable(
            text = title.get(),
            style = AppTypography.titleMedium22,
            color = AppTheme.colors.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        VSpacer(AppDimension.layoutMainMargin)
        TextComposable(
            text = description.get(),
            style = AppTypography.titleRegular16,
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