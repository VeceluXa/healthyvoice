package com.danilovfa.presentation.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.danilovfa.presentation.analysis.waveform.RecordingWaveform
import com.danilovfa.presentation.analysis.model.ParameterDataUi
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.WSpacer
import com.danilovfa.common.uikit.composables.dialog.AlertDialog
import com.danilovfa.common.uikit.composables.state.LoaderStub
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.composables.toolbar.NavigationIcon
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.event.ObserveEvents
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography


@Composable
fun AnalyzeScreen(
    component: AnalyzeComponent
) {
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogDelegate.alertDialogStateFlow.collectAsState()

    ObserveEvents(component.eventDelegate)

    AnalyzeLayout(
        state = state,
        onIntent = component::onIntent
    )

    AlertDialog(alertDialogState)
}

@Composable
private fun AnalyzeLayout(
    state: State,
    onIntent: (Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .systemBarsPadding()
    ) {
        Toolbar(
            title = stringResource(strings.analyze_title),
            navigationIcon = NavigationIcon.Back,
            onNavigationClick = { onIntent(Intent.OnBackClicked) }
        )

        when {
            state.isRecordingLoading -> LoaderStub(Modifier.weight(1f))
            else -> AnalyzeContent(
                state = state,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnalyzeContent(
    state: State,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        VSpacer(AppDimension.layoutMainMargin)
        
        if (state.amplitudes.isNotEmpty()) {
            RecordingWaveform(
                amplitudes = state.amplitudes,
                cutStartMillis = state.recordingAnalysis?.recording?.cutStart,
                cutEndMillis = state.recordingAnalysis?.recording?.cutEnd,
                durationMillis = state.recordingAnalysis?.recording?.durationMillis ?: 0
            )
        }
        
        VSpacer(AppDimension.layoutLargeMargin)

        if (state.isAnalysisLoading) {
            LoaderStub(
                text = stringResource(strings.analyze_loading)
            )
        } else {
            Text(
                text = stringResource(strings.analyze_voice_parameters),
                style = AppTypography.titleMedium20,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )

            VSpacer(AppDimension.layoutMainMargin)

            ParametersContent(parameters = state.parameters)
        }

    }
}


@Composable
private fun ParametersContent(
    parameters: List<ParameterDataUi>,
    modifier: Modifier = Modifier
) {
    val primaryBackgroundColor = AppTheme.colors.surface
    val secondaryBackgroundColor = AppTheme.colors.primaryDisabled.copy(alpha = 0.1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(4f / 6f))
            Text(
                text = stringResource(strings.analyze_norm),
                textAlign = TextAlign.Center,
                style = AppTypography.titleMedium20,
                modifier = Modifier.weight(2f / 6f)
            )
        }

        parameters.forEachIndexed { index, parameterDataUi ->
            ParameterItem(
                data = parameterDataUi,
                backgroundColor = if (index % 2 == 0) primaryBackgroundColor else secondaryBackgroundColor
            )
        }
    }
}

@Composable
private fun ParameterItem(
    data: ParameterDataUi,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.colors.background
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                horizontal = AppDimension.toolbarHorizontalMargin,
                vertical = AppDimension.layoutMediumMargin
            )
    ) {
        Text(
            text = data.label,
            style = AppTypography.bodyMedium16,
            modifier = Modifier.weight(1f)
        )

        Row(modifier = Modifier.weight(1f)) {
            Text(
                text = "%.2f".format(data.value),
                textAlign = TextAlign.Center,
                style = AppTypography.bodyRegular16,
                modifier = Modifier.weight(1f)
            )

            data.normMin?.let { normMin ->
                Text(
                    text = "%.2f".format(normMin),
                    textAlign = TextAlign.Center,
                    style = AppTypography.bodyRegular16,
                    modifier = Modifier.weight(1f)
                )
            } ?: WSpacer()

            data.normMax?.let { normMax ->
                Text(
                    text = "%.2f".format(normMax),
                    textAlign = TextAlign.Center,
                    style = AppTypography.bodyRegular16,
                    modifier = Modifier.weight(1f)
                )
            } ?: WSpacer()
        }
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        AnalyzeLayout(
            state = State(recordingId = 0),
            onIntent = {}
        )
    }
}