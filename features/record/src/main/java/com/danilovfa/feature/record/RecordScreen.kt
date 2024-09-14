package com.danilovfa.feature.record

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danilovfa.core.library.context.launchAppSettings
import com.danilovfa.core.library.time.tickerInstant
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.State
import com.danilovfa.libs.recorder.utils.AudioConstants
import com.danilovfa.resources.drawable.AppIcon
import com.danilovfa.resources.drawable.strings
import com.danilovfa.uikit.composables.animation.AnimatedVisibilityNullableValue
import com.danilovfa.uikit.composables.dialog.AlertDialog
import com.danilovfa.uikit.composables.event.ObserveEvents
import com.danilovfa.uikit.composables.event.permission.ObserveRequestPermissionEvents
import com.danilovfa.uikit.composables.toolbar.Toolbar
import com.danilovfa.uikit.theme.AppDimension
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography
import kotlinx.datetime.Instant

@Composable
fun RecordScreen(
    component: RecordComponent
) {
    val context = LocalContext.current
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogStateFlow.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            component.onIntent(Intent.OnRecordStartClicked)
        }

    ObserveEvents(component.events) { event ->
        when (event) {
            is RecordComponent.Events.OpenAppSettings -> {
                launcher.launchAppSettings(context.packageName)
                true
            }

            else -> false
        }
    }

    ObserveRequestPermissionEvents(
        requestPermissionEvents = component.requestPermissionFlow,
        onPermissionResult = { component.onIntent(Intent.OnPermissionStatusChanged(it)) }
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
            .background(AppTheme.colors.backgroundPrimary)
            .systemBarsPadding()
    ) {
        Toolbar(
            navigationIcon = null,
            onNavigationClick = {},
            title = stringResource(strings.app_title),
            startIcon = {
                Image(
                    painter = AppIcon.AppIcon,
                    contentDescription = "App",
                    modifier = Modifier.padding(start = AppDimension.layoutHorizontalMargin)
                )
            },
            actions = {
                IconButton(
                    onClick = { onIntent(Intent.OnShowHelpDialogClicked) }
                ) {
                    Icon(
                        painter = AppIcon.Question,
                        tint = AppTheme.colors.textDisabled,
                        contentDescription = "Help",
                        modifier = Modifier
                            .background(
                                color = AppTheme.colors.surface,
                                shape = CircleShape
                            )
                            .padding(AppDimension.layoutSmallMargin)
                    )
                }
            }
        )
        RecordContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.weight(1f)
        )
    }
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
    val density = LocalDensity.current

    val amplitudeWidth = 5.dp
    val distanceBetweenAmplitudes = 3.dp
    val amplitudeColor = AppTheme.colors.buttonSecondary
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
            .offset(x = -graphOffset)
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
//    Log.d("RecordGraph", height.toString())
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
    ) { startTime ->
        val currentTime by tickerInstant(delay = 10L)
        val diff = currentTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()

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
        tint = AppTheme.colors.buttonSecondary,
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
private fun Preview() {
    AppTheme {
        RecordLayout(
            state = State(),
            onIntent = { }
        )
    }
}