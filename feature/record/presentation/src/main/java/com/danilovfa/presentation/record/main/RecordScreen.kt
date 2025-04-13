package com.danilovfa.presentation.record.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.core.presentation.time.tickerInstant
import com.danilovfa.presentation.record.main.store.RecordStore.Intent
import com.danilovfa.presentation.record.main.store.RecordStore.State
import com.danilovfa.libs.recorder.utils.AudioConstants
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.animation.AnimatedVisibilityNullableValue
import com.danilovfa.common.uikit.composables.dialog.AlertDialog
import com.danilovfa.common.uikit.composables.popup.MenuItemsData
import com.danilovfa.common.uikit.composables.popup.PopupMenu
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.event.ObserveEvents
import com.danilovfa.common.uikit.event.ObserveRequestPermissionEvents
import com.danilovfa.common.uikit.preview.ThemePreviewParameter
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.common.core.presentation.extensions.launchAppSettings
import kotlinx.datetime.Instant
import android.content.Intent as AndroidIntent

@Composable
internal fun RecordScreen(
    component: RecordComponent
) {
    val context = LocalContext.current
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogDelegate.alertDialogStateFlow.collectAsState()

    val settingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            component.onIntent(Intent.OnRecordStartClicked)
        }

    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                component.onIntent(Intent.OnRecordImported(context, uri))
            }
        }

    ObserveEvents(component.eventDelegate) { event ->
        when (event) {
            is RecordComponent.RecordEvent.OpenAppSettings -> {
                settingsLauncher.launchAppSettings(context)
                true
            }

            is RecordComponent.RecordEvent.OpenFilePicker -> {
                val intent = AndroidIntent(AndroidIntent.ACTION_GET_CONTENT).apply {
                    type = "audio/*"
                    addCategory(AndroidIntent.CATEGORY_OPENABLE)
                }

                filePickerLauncher.launch(intent)

                true
            }

            else -> false
        }
    }

    ObserveRequestPermissionEvents(
        requestPermissionEvents = component.requestPermissionEventDelegate.requestPermissionFlow,
        onPermissionResult = {
            component.onIntent(Intent.OnPermissionStatusChanged(it))
        }
    )

    RecordLayout(
        state = state,
        onIntent = component::onIntent
    )

    AlertDialog(alertDialogState)
}

@Composable
private fun RecordLayout(
    state: State,
    onIntent: (Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .systemBarsPadding()
    ) {
        RecordToolbar(
            onShowHelpDialogClicked = { onIntent(Intent.OnShowHelpDialogClicked) },
            onImportClicked = { onIntent(Intent.OnImportRecordingClicked) }
        )
        RecordContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RecordToolbar(
    onShowHelpDialogClicked: () -> Unit,
    onImportClicked: () -> Unit
) {
    val isMenuExpanded = remember { mutableStateOf(false) }

    Toolbar(
        navigationIcon = null,
        onNavigationClick = {},
        title = stringResource(strings.record_title),
        actions = {
            IconButton(
                onClick = { isMenuExpanded.value = true }
            ) {
                Icon(
                    painter = AppIcon.VerticalMore,
                    tint = AppTheme.colors.primary,
                    contentDescription = "More",
                )
            }

            PopupMenu(
                MenuItemsData(
                    title = Text.Resource(strings.record_menu_import),
                    icon = AppIcon.Import,
                    onClick = onImportClicked
                ),
                MenuItemsData(
                    title = Text.Resource(strings.record_menu_help),
                    icon = AppIcon.Help,
                    onClick = onShowHelpDialogClicked
                ),
                expandedState = isMenuExpanded
            )
        }
    )
}

@Composable
private fun RecordContent(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        RecordButton(
            isRecording = state.isRecording,
            onRecordClick = { onIntent(Intent.OnRecordStartClicked) },
            onStopRecordClick = { onIntent(Intent.OnRecordStopClicked) },
            modifier = Modifier.align(Alignment.Center)
        )

        Timer(
            startTime = state.recordingStartTime,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(AppDimension.layoutMainMargin)
        )

        RecordingGraph(
            amplitudes = state.amplitudes,
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimension.layoutMainMargin)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RecordingGraph(
    amplitudes: List<Int>,
    modifier: Modifier = Modifier
) {
    val amplitudeWidth = 5.dp
    val distanceBetweenAmplitudes = 3.dp
    val amplitudeColor = AppTheme.colors.primary
    val graphHeight = 200.dp

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    val graphOffset by animateDpAsState(
        targetValue = if (amplitudes.size * (amplitudeWidth.value + distanceBetweenAmplitudes.value) >
            screenWidthDp.value
        ) {
            ((amplitudeWidth.value + distanceBetweenAmplitudes.value) * amplitudes.size - screenWidthDp.value).dp
        } else 0.dp,
        label = "RecordingGraphOffset"
    )

    Canvas(
        modifier = modifier
            .height(graphHeight)
            .fillMaxWidth()
            .offset { IntOffset(x = (-graphOffset).roundToPx(), y = 0) }
    ) {
        amplitudes.forEachIndexed { i, amplitude ->
            val amplitudeHeight = getAmplitudeHeight(
                amplitude = amplitude,
                graphHeight = graphHeight.toPx()
            )

            drawRoundRect(
                color = amplitudeColor,
                topLeft = Offset(
                    x = (amplitudeWidth.toPx() + distanceBetweenAmplitudes.toPx()) * i,
                    y = (graphHeight.toPx() / 2) - (amplitudeHeight / 2)
                ),
                size = Size(
                    width = amplitudeWidth.toPx(),
                    height = amplitudeHeight
                ),
                cornerRadius = CornerRadius(x = 100f, y = 100f)
            )
        }
    }
}

private fun getAmplitudeHeight(
    amplitude: Int,
    graphHeight: Float
): Float {
    val height = graphHeight * amplitude / (AudioConstants.MAX_AMPLITUDE)
    return height
}

@Composable
private fun Timer(
    startTime: Instant?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibilityNullableValue(
        value = startTime,
        modifier = modifier
    ) { initialTime ->
        val currentTime by tickerInstant(delay = 10L)
        val diff = currentTime.toEpochMilliseconds() - initialTime.toEpochMilliseconds()

        val millis = (diff % 1000).toString().padStart(3, '0')
        val seconds = (diff / 1000).toString().padStart(1, '0')
        val formattedTime = "$seconds:$millis"

        Text(
            text = formattedTime,
            style = AppTypography.bodyRegular16.copy(fontFamily = FontFamily.Monospace),
        )

    }
}

@Composable
private fun RecordButton(
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    onStopRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = if (!isRecording) AppIcon.Play else AppIcon.Stop,
        tint = AppTheme.colors.primary,
        contentDescription = "Play",
        modifier = modifier
            .size(100.dp)
            .clip(CircleShape)
            .clickable(
                onClick = {
                    if (isRecording) {
                        onStopRecordClick()
                    } else {
                        onRecordClick()
                    }
                }
            )
            .padding(AppDimension.layoutMediumMargin)
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) isDark: Boolean) {
    AppTheme(isDark) {
        RecordLayout(
            state = State(patientId = 0),
            onIntent = { }
        )
    }
}