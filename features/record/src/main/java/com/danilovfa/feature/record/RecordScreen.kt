package com.danilovfa.feature.record

import android.graphics.Path
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danilovfa.core.library.context.launchAppSettings
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.State
import com.danilovfa.resources.drawable.AppIcon
import com.danilovfa.resources.drawable.strings
import com.danilovfa.uikit.composables.animation.IconAnimatedVisibility
import com.danilovfa.uikit.composables.dialog.AlertDialog
import com.danilovfa.uikit.composables.event.ObserveEvents
import com.danilovfa.uikit.composables.event.permission.ObserveRequestPermissionEvents
import com.danilovfa.uikit.composables.toolbar.Toolbar
import com.danilovfa.uikit.theme.AppDimension
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.AppTypography
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun RecordScreen(
    component: RecordComponent
) {
    val context = LocalContext.current
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogStateFlow.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            component.onIntent(Intent.OnRecordClicked)
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
            title = stringResource(strings.app_title),
            startIcon = {
                Image(
                    painter = AppIcon.AppIcon,
                    contentDescription = "App",
                    modifier = Modifier.padding(start = AppDimension.layoutHorizontalMargin)
                )
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
            onRecordClick = { onIntent(Intent.OnRecordClicked) },
            modifier = Modifier.align(Alignment.Center)
        )

        Timer(
            time = state.recordingTimeMillis,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(AppDimension.layoutMainMargin)
        )
    }
}

@Composable
private fun RecordingGraph(
    amplitudes: SnapshotStateList<Int>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        draw
        amplitudes.forEach {

        }
    }

}

@Composable
private fun Timer(
    time: Long,
    modifier: Modifier = Modifier
) {
    IconAnimatedVisibility(
        visible = time != 0L,
        modifier = modifier
    ) {
        val millis = (time % 1000).toString().padStart(3, '0')
        val seconds = (time / 1000).toString().padStart(1, '0')
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
    modifier: Modifier = Modifier
) {
    Icon(
        painter = if (!isRecording) AppIcon.Play else AppIcon.Pause,
        tint = AppTheme.colors.buttonSecondary,
        contentDescription = "Play",
        modifier = modifier
            .size(100.dp)
            .clip(CircleShape)
            .clickable(
                onClick = onRecordClick
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

илья🆓, [29.01.2024 14:28]
/**
 * Draws waveform for the provided sound amplitudes.
 * Draws a line for the positions between [minScrollDelta]..0.
 *
 * Internally calculate distance between spikes.
 * Takes [minScrollDelta] as the maximum width of the canvas to draw all spikes.
 *
 * Applies bezier interpolation to the waveform when it can be done according to the performance limitations.
 *
 * Size must be set EXPLICITLY because of using [Canvas].
 *
 * @param amplitudes sound amplitudes.
 * @param scrollDelta current scroll delta, in pixels.
 * @param minScrollDelta position in pixels where the last amplitude should be drawn.
 * @param bezierIntensity intensity for the bezier interpolation algorithm.
 *
 * @see BezierPerformanceLimit
 */
@Composable
internal fun Waveform(
    amplitudes: WaveformAmplitudes,
    brush: Brush,
    scrollDelta: Float,
    minScrollDelta: Float,
    modifier: Modifier = Modifier,
    bezierIntensity: Float = 0.35f,
) {
    Canvas(modifier = modifier) {
        val spikeDistance = abs(minScrollDelta) / amplitudes.values.size
        val spikesOnScreen = ceil(size.width / spikeDistance).toInt()
        val sideSpikesCount = ceil(spikesOnScreen / 2f).toInt()

        val middleSpikeIndex = floor(-scrollDelta / spikeDistance).toInt()
        val minVisibleSpikeIndex = middleSpikeIndex - sideSpikesCount
        val maxVisibleSpikeIndex = middleSpikeIndex + (sideSpikesCount * OffscreenSpikesMultiplier).toInt()
        val spikesIndices = minVisibleSpikeIndex..maxVisibleSpikeIndex

        val startX = scrollDelta % spikeDistance
        val path = Path().apply {
            if (spikesOnScreen > BezierPerformanceLimit) waveform(
                amplitudes = amplitudes.values,
                visibleSpikesIndexes = spikesIndices,
                spikeDistance = spikeDistance,
                startX = startX,
                startY = center.y,
            ) else bezierInterpolatedWaveform(
                amplitudes = amplitudes.values,
                visibleSpikesIndexes = spikesIndices,
                spikeDistance = spikeDistance,
                startX = startX,
                startY = center.y,
                bezierIntensity = bezierIntensity,
            )
        }

        drawPath(
            path = path,
            brush = brush,
        )
        flip(FlipDirection.Vertical) {
            drawPath(
                path = path,
                brush = brush,
            )
        }
        drawLine(
            brush = brush,
            start = Offset(0f, size.center.y),
            end = Offset(size.width, size.center.y),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

/**
 * Fills the path with visible waveform amplitudes
 */
private fun Path.waveform(
    amplitudes: List<Float>,
    visibleSpikesIndexes: IntRange,
    spikeDistance: Float,
    startX: Float,
    startY: Float,
) {
    moveTo(x = startX, y = startY)
    visibleSpikesIndexes.forEachIndexed { index, spikeIndex ->
        val amplitude = amplitudes.getOrNull(spikeIndex) ?: EmptyAmplitudePlaceholder // draw line for the empty values
        val curX = startX + spikeDistance * index
        val curY = amplitude.getY(startY)
        lineTo(x = curX, y = curY)
    }
    lineTo(x = startX + spikeDistance * visibleSpikesIndexes.size, y = startY)
    close()
}


/**
 * Fills the path with visible waveform amplitudes.
 * Applies bezier interpolation to the waveform.
 *
 * it can have a bad effect on performance with a large amount of data.
 */
private fun Path.bezierInterpolatedWaveform(
    amplitudes: List<Float>,
    visibleSpikesIndexes: IntRange,
    spikeDistance: Float,
    startX: Float,
    startY: Float,
    bezierIntensity: Float,
) {
    moveTo(x = startX, y = startY)
    visibleSpikesIndexes.forEachIndexed { index, spikeIndex ->
        val curX = startX + spikeDistance * index
        val amplitude = amplitudes.getOrNull(spikeIndex) ?: run {
            lineTo(x = curX, y = EmptyAmplitudePlaceholder.getY(startY)) // draw line for the empty values
            return@forEachIndexed
        }
        val curY = amplitude.getY(startY)

        val prevAmplitude = amplitudes.getOrNull(spikeIndex - 1) ?: run {
            lineTo(x = curX, y = curY) // draw line for the first item
            return@forEachIndexed
        }
        val prevX = curX - spikeDistance
        val prevY = prevAmplitude.getY(startY)

        cubicTo(
            x1 = prevX + spikeDistance * bezierIntensity,
            y1 = prevY,
            x2 = curX - spikeDistance * bezierIntensity,
            y2 = curY,
            x3 = curX,
            y3 = curY,
        )
    }
    lineTo(x = spikeDistance * visibleSpikesIndexes.size, y = startY)
    close()
}