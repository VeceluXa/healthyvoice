package com.danilovfa.presentation.cut

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.danilovfa.presentation.cut.composable.CutGraph
import com.danilovfa.presentation.cut.store.CutStore.Intent
import com.danilovfa.presentation.cut.store.CutStore.State
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.WSpacer
import com.danilovfa.common.uikit.composables.button.ButtonLarge
import com.danilovfa.common.uikit.composables.dialog.AlertDialog
import com.danilovfa.common.uikit.composables.state.LoaderStub
import com.danilovfa.common.uikit.composables.toolbar.NavigationIcon
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.domain.record.repository.model.AudioData
import java.util.UUID

@Composable
fun CutScreen(
    component: CutComponent
) {
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogDelegate.alertDialogStateFlow.collectAsState()

    CutLayout(
        state = state,
        onIntent = component::onIntent
    )

    AlertDialog(alertDialogState)
}

@Composable
private fun CutLayout(
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
            title = stringResource(strings.cut_title),
            navigationIcon = NavigationIcon.Back,
            onNavigationClick = { onIntent(Intent.OnBackClicked) }
        )

        if (state.amplitudes.isNotEmpty()) {
            CutContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier.weight(1f)
            )
        } else {
            LoaderStub(Modifier.weight(1f))
        }
    }
}

@Composable
private fun CutContent(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        VSpacer(AppDimension.layoutExtraLargeMargin)
        CutGraph(
            amplitudes = state.amplitudes,
            startOffsetMillis = state.startOffset,
            endOffsetMillis = state.endOffset,
            durationMillis = state.data.audioDurationMillis,
            onStartOffsetChanged = { onIntent(Intent.OnStartOffsetMoved(it)) },
            onEndOffsetChanged = { onIntent(Intent.OnEndOffsetMoved(it)) }
        )

        WSpacer()
        ButtonLarge(
            text = stringResource(strings.cut_analyze_button),
            onClick = { onIntent(Intent.OnAnalyzeClicked) },
            modifier = Modifier.padding(AppDimension.layoutMainMargin)
        )
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        CutLayout(
            state = State(
                data = AudioData(
                    filename = UUID.randomUUID().toString(),
                    bitsPerSample = 0,
                    frequency = 0,
                    bufferSize = 0,
                    channels = 1,
                    audioDurationMillis = 0
                )
            ),
            onIntent = {}
        )
    }
}