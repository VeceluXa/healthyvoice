package com.danilovfa.presentation.analysis.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.danilovfa.common.uikit.theme.AppTheme
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

internal const val WAVEFORM_HEIGHT_DP = 300
private const val SPACE_BETWEEN_DOTS = 5
private const val MAX_ZOOM = 8f

private const val EmptyAmplitudePlaceholder = 0f
private const val OffscreenSpikesMultiplier = 2
private const val BezierPerformanceLimit = 500

@Composable
internal fun RecordingWaveform(
    amplitudes: List<Short>,
    cutStartMillis: Int?,
    cutEndMillis: Int?,
    durationMillis: Int,
    modifier: Modifier = Modifier,
    brush: Brush = SolidColor(AppTheme.colors.primary),
    bezierIntensity: Float = 0.35f,
) {
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    var scale by remember {
        mutableFloatStateOf(1f)
    }

    var scrollDelta by remember {
        mutableFloatStateOf(-0f)//-(screenWidthPx / 2))
    }

    val waveformController = rememberWaveformController(amplitudes) { prev, new ->
        scrollDelta = (scrollDelta / prev * new * scale)
    }

    val minScale = screenWidthPx / waveformController.points.size.toFloat()

    val minScrollDelta by remember {
        derivedStateOf {
            (waveformController.points.size.toFloat() * scale).coerceAtLeast(screenWidthPx)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(WAVEFORM_HEIGHT_DP.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (scale * zoom in minScale..MAX_ZOOM) {
                        scale *= zoom

                        //waveformController.scale(scale)
                        Logger
                            .withTag("Waveform")
                            .d("Amplitudes Size: ${waveformController.points.size}")

                        scrollDelta =
                            ((scrollDelta + pan.x) * zoom).coerceIn((-minScrollDelta + screenWidthPx)..0f)
                    } else {
                        scrollDelta =
                            (scrollDelta + pan.x).coerceIn((-minScrollDelta + screenWidthPx)..0f)
                    }

                    Logger
                        .withTag("Waveform")
                        .d("Zoom: $scale, Pan: $scrollDelta")
                }
            }
    ) {
        Waveform(
            points = waveformController.points,
            scrollDelta = scrollDelta,
            minScrollDelta = minScrollDelta,
            cutStartMillis = cutStartMillis,
            cutEndMillis = cutEndMillis,
            durationMillis = durationMillis,
            amplitudesBrush = brush,
            bezierIntensity = bezierIntensity,
            modifier = Modifier
                .matchParentSize()
        )
    }
}

@Composable
private fun Waveform(
    points: List<Float>,
    scrollDelta: Float,
    minScrollDelta: Float,
    cutStartMillis: Int?,
    cutEndMillis: Int?,
    durationMillis: Int,
    modifier: Modifier = Modifier,
    amplitudesBrush: Brush = SolidColor(AppTheme.colors.primary),
    cutBrush: Brush = SolidColor(AppTheme.colors.indicator),
    bezierIntensity: Float = 0.35f
) {
    val waveformHeightPx = with(LocalDensity.current) {
        WAVEFORM_HEIGHT_DP.dp.toPx()
    }

    Canvas(modifier = modifier) {
        val spikeDistance = abs(minScrollDelta) / points.size
        val spikesOnScreen = ceil(size.width / spikeDistance).toInt()
        val sideSpikesCount = ceil(spikesOnScreen / 2f).toInt()

        val middleSpikeIndex = floor((-scrollDelta + size.width / 2) / spikeDistance).toInt()
        val minVisibleSpikeIndex = middleSpikeIndex - sideSpikesCount
        val maxVisibleSpikeIndex =
            middleSpikeIndex + (sideSpikesCount)// * OffscreenSpikesMultiplier)
        val spikesIndices = minVisibleSpikeIndex..maxVisibleSpikeIndex

        val startX = 0f //scrollDelta % spikeDistance
        val path = Path().apply {
            if (spikesOnScreen > BezierPerformanceLimit) waveform(
                amplitudes = points,
                visibleSpikesIndexes = spikesIndices,
                spikeDistance = spikeDistance,
                startX = startX,
                startY = center.y,
                waveformHeightPx = waveformHeightPx
            ) else bezierInterpolatedWaveform(
                amplitudes = points,
                visibleSpikesIndexes = spikesIndices,
                spikeDistance = spikeDistance,
                startX = startX,
                startY = center.y,
                bezierIntensity = bezierIntensity,
                waveformHeightPx = waveformHeightPx
            )
        }

        drawPath(
            path = path,
            brush = amplitudesBrush,
        )

        drawLine(
            brush = amplitudesBrush,
            start = Offset(0f, size.center.y),
            end = Offset(size.width, size.center.y),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
        )

        drawCutLines(
            cutStartMillis = cutStartMillis,
            cutEndMillis = cutEndMillis,
            durationMillis = durationMillis,
            minScrollDelta = minScrollDelta,
            scrollDelta = scrollDelta,
            brush = cutBrush
        )
    }
}

private fun DrawScope.drawCutLines(
    cutStartMillis: Int?,
    cutEndMillis: Int?,
    durationMillis: Int,
    minScrollDelta: Float,
    scrollDelta: Float,
    brush: Brush
) {
    val startOffsetPx =
        cutStartMillis?.let { it.toFloat() / durationMillis * minScrollDelta + scrollDelta }
    val endOffsetPx =
        cutEndMillis?.let { it.toFloat() / durationMillis * minScrollDelta + scrollDelta }

    listOfNotNull(startOffsetPx, endOffsetPx)
        .forEach { offset ->
            drawLine(
                brush = brush,
                start = Offset(
                    x = offset,
                    y = 0f
                ),
                end = Offset(
                    x = offset,
                    y = WAVEFORM_HEIGHT_DP.dp.toPx()
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
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
    waveformHeightPx: Float,
) {
    moveTo(x = startX, y = startY)
    visibleSpikesIndexes.forEachIndexed { index, spikeIndex ->
        val amplitude = amplitudes.getOrNull(spikeIndex)
            ?: EmptyAmplitudePlaceholder // draw line for the empty values
        val curX = startX + spikeDistance * index
        val curY = amplitude.getY(startY, waveformHeightPx)
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
    waveformHeightPx: Float,
) {
    moveTo(x = startX, y = startY)
    visibleSpikesIndexes.forEachIndexed { index, spikeIndex ->
        val curX = startX + spikeDistance * index
        val amplitude = amplitudes.getOrNull(spikeIndex) ?: run {
            lineTo(
                x = curX,
                y = EmptyAmplitudePlaceholder.getY(startY, waveformHeightPx)
            ) // draw line for the empty values
            return@forEachIndexed
        }
        val curY = amplitude.getY(startY, waveformHeightPx)

        val prevAmplitude = amplitudes.getOrNull(spikeIndex - 1) ?: run {
            lineTo(x = curX, y = curY) // draw line for the first item
            return@forEachIndexed
        }
        val prevX = curX - spikeDistance
        val prevY = prevAmplitude.getY(startY, waveformHeightPx)

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

private fun Float.getY(startY: Float, heightPx: Float): Float {
    return startY + this * heightPx / 2
}

private val IntRange.size get() = endInclusive - start + 1


