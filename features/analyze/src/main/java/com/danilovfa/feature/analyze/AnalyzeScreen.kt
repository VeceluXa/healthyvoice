package com.danilovfa.feature.analyze

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.danilovfa.data.common.model.AudioData
import com.danilovfa.feature.analyze.waveform.WAVEFORM_HEIGHT_DP
import com.danilovfa.feature.analyze.waveform.RecordingWaveform
import com.danilovfa.feature.analyze.model.AnalyzeParametersUi
import com.danilovfa.feature.analyze.store.AnalyzeStore.Intent
import com.danilovfa.feature.analyze.store.AnalyzeStore.State
import com.danilovfa.resources.drawable.strings
import com.danilovfa.uikit.composables.VSpacer
import com.danilovfa.uikit.composables.dialog.AlertDialog
import com.danilovfa.uikit.composables.event.ObserveEvents
import com.danilovfa.uikit.composables.state.LargeShimmerItem
import com.danilovfa.uikit.composables.state.LoaderStub
import com.danilovfa.uikit.composables.state.ShimmerItem
import com.danilovfa.uikit.composables.text.Text
import com.danilovfa.uikit.composables.toolbar.NavigationIcon
import com.danilovfa.uikit.composables.toolbar.Toolbar
import com.danilovfa.uikit.theme.AppDimension
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography
import java.util.UUID


@Composable
fun AnalyzeScreen(
    component: AnalyzeComponent
) {
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogStateFlow.collectAsState()

    ObserveEvents(component.events)

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
            .background(AppTheme.colors.backgroundPrimary)
            .systemBarsPadding()
    ) {
        Toolbar(
            title = stringResource(strings.analyze_title),
            navigationIcon = NavigationIcon.Back,
            onNavigationClick = { onIntent(Intent.OnBackClicked) }
        )

        when {
            state.isLoading -> LoaderStub(Modifier.weight(1f))
            else -> AnalyzeContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnalyzeContent(
    state: State,
    onIntent: (Intent) -> Unit,
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
                cut = state.audioData.audioCut,
                durationMillis = state.audioData.audioDurationMillis
            )
        } else {
            LargeShimmerItem(
                height = WAVEFORM_HEIGHT_DP.dp,
                modifier = Modifier
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
        }
        
        VSpacer(AppDimension.layoutLargeMargin)

        Text(
            text = stringResource(strings.analyze_voice_parameters),
            style = AppTypography.titleMedium20,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimension.layoutHorizontalMargin)
        )

        VSpacer(AppDimension.layoutMainMargin)
        
        if (state.parameters != null) {
            ParametersContent(parameters = state.parameters)
        } else {
            ParametersLoader()
        }
    }
}


@Composable
private fun ParametersContent(
    parameters: AnalyzeParametersUi,
    modifier: Modifier = Modifier
) {
    val primaryBackgroundColor = AppTheme.colors.surface
    val secondaryBackgroundColor = AppTheme.colors.buttonSecondary.copy(alpha = 0.1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        ParameterItem(
            label = "J1",
            value = parameters.j1,
            backgroundColor = primaryBackgroundColor
        )
        ParameterItem(
            label = "J3",
            value = parameters.j3,
            backgroundColor = secondaryBackgroundColor
        )
        ParameterItem(
            label = "J5",
            value = parameters.j5,
            backgroundColor = primaryBackgroundColor
        )
        ParameterItem(
            label = "S1",
            value = parameters.s1,
            backgroundColor = secondaryBackgroundColor
        )
        ParameterItem(
            label = "S3",
            value = parameters.s3,
            backgroundColor = primaryBackgroundColor
        )
        ParameterItem(
            label = "S5",
            value = parameters.s5,
            backgroundColor = secondaryBackgroundColor
        )
        ParameterItem(
            label = "S11",
            value = parameters.s11,
            backgroundColor = primaryBackgroundColor
        )
    }
}

@Composable
private fun ParametersLoader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = AppDimension.layoutHorizontalMargin)
    ) {
        repeat(7) {
            ShimmerItem(
                size = DpSize(
                    width = (100..140).random().dp,
                    height = 24.dp
                )
            )
            VSpacer(AppDimension.layoutMediumMargin)
        }
    }
}

@Composable
private fun ParameterItem(
    label: String,
    value: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.colors.backgroundPrimary
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
            text = "${label}: " + "%.2f".format(value),
            style = AppTypography.bodyRegular16
        )
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        AnalyzeLayout(
            state = State(
                audioData = AudioData(
                    filename = UUID.randomUUID().toString(),
                    frequency = 0,
                    channels = 0,
                    bitsPerSample = 0,
                    bufferSize = 0,
                    audioDurationMillis = 0
                )
            ),
            onIntent = {}
        )
    }
}