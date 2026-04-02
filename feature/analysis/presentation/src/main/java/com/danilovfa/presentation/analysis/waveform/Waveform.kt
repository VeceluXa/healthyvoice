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
import com.danilovfa.common.uikit.theme.AppTheme

internal const val WAVEFORM_HEIGHT_DP = 300
private const val MAX_ZOOM = 8f
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

    var scale by remember(amplitudes) {
        mutableFloatStateOf(1f)
    }

    var scrollDelta by remember(amplitudes) {
        mutableFloatStateOf(0f)
    }

    val waveformController = rememberWaveformController(amplitudes)
    val pointCount = waveformController.points.size

    if (pointCount == 0) {
        return
    }

    val minScale = minOf(1f, screenWidthPx / pointCount.toFloat())

    val contentWidthPx by remember(pointCount, scale, screenWidthPx) {
        derivedStateOf {
            (pointCount.toFloat() * scale).coerceAtLeast(screenWidthPx)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(WAVEFORM_HEIGHT_DP.dp)
            .pointerInput(minScale, pointCount, screenWidthPx) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val nextScale = (scale * zoom).coerceIn(minScale, MAX_ZOOM)
                    val appliedZoom = nextScale / scale
                    val nextContentWidthPx =
                        (pointCount.toFloat() * nextScale).coerceAtLeast(screenWidthPx)

                    scrollDelta = if (appliedZoom != 1f) {
                        ((scrollDelta + pan.x) * appliedZoom)
                            .coerceIn((-nextContentWidthPx + screenWidthPx)..0f)
                    } else {
                        (scrollDelta + pan.x)
                            .coerceIn((-nextContentWidthPx + screenWidthPx)..0f)
                    }
                    scale = nextScale
                }
            }
    ) {
        Waveform(
            points = waveformController.points,
            scrollDelta = scrollDelta,
            contentWidthPx = contentWidthPx,
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
    contentWidthPx: Float,
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
        val viewport = calculateWaveformViewport(
            pointCount = points.size,
            canvasWidthPx = size.width,
            scrollDelta = scrollDelta,
            contentWidthPx = contentWidthPx,
        ) ?: return@Canvas
        val plotPoints = buildWaveformPlotPoints(
            amplitudes = points,
            viewport = viewport,
        )

        if (plotPoints.isNotEmpty()) {
            val path = Path().apply {
                if (plotPoints.size > BezierPerformanceLimit) {
                    waveform(
                        plotPoints = plotPoints,
                        startY = center.y,
                        waveformHeightPx = waveformHeightPx,
                    )
                } else {
                    bezierInterpolatedWaveform(
                        plotPoints = plotPoints,
                        startY = center.y,
                        bezierIntensity = bezierIntensity,
                        waveformHeightPx = waveformHeightPx,
                    )
                }
            }

            drawPath(
                path = path,
                brush = amplitudesBrush,
            )
        }

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
            contentWidthPx = contentWidthPx,
            scrollDelta = scrollDelta,
            brush = cutBrush
        )
    }
}

private fun DrawScope.drawCutLines(
    cutStartMillis: Int?,
    cutEndMillis: Int?,
    durationMillis: Int,
    contentWidthPx: Float,
    scrollDelta: Float,
    brush: Brush
) {
    val startOffsetPx = calculateCutLineOffset(
        cutMillis = cutStartMillis,
        durationMillis = durationMillis,
        contentWidthPx = contentWidthPx,
        scrollDelta = scrollDelta,
    )
    val endOffsetPx = calculateCutLineOffset(
        cutMillis = cutEndMillis,
        durationMillis = durationMillis,
        contentWidthPx = contentWidthPx,
        scrollDelta = scrollDelta,
    )

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
    plotPoints: List<WaveformPlotPoint>,
    startY: Float,
    waveformHeightPx: Float,
) {
    if (plotPoints.isEmpty()) {
        return
    }

    val firstPoint = plotPoints.first()
    moveTo(x = firstPoint.x, y = startY)
    lineTo(x = firstPoint.x, y = firstPoint.amplitude.getY(startY, waveformHeightPx))

    for (index in 1 until plotPoints.size) {
        val point = plotPoints[index]
        lineTo(x = point.x, y = point.amplitude.getY(startY, waveformHeightPx))
    }

    lineTo(x = plotPoints.last().x, y = startY)
    close()
}

/**
 * Fills the path with visible waveform amplitudes.
 * Applies bezier interpolation to the waveform.
 *
 * it can have a bad effect on performance with a large amount of data.
 */
private fun Path.bezierInterpolatedWaveform(
    plotPoints: List<WaveformPlotPoint>,
    startY: Float,
    bezierIntensity: Float,
    waveformHeightPx: Float,
) {
    if (plotPoints.isEmpty()) {
        return
    }

    val firstPoint = plotPoints.first()
    moveTo(x = firstPoint.x, y = startY)
    lineTo(x = firstPoint.x, y = firstPoint.amplitude.getY(startY, waveformHeightPx))

    for (index in 1 until plotPoints.size) {
        val prevPoint = plotPoints[index - 1]
        val curPoint = plotPoints[index]
        val prevY = prevPoint.amplitude.getY(startY, waveformHeightPx)
        val curY = curPoint.amplitude.getY(startY, waveformHeightPx)
        val spikeDistance = curPoint.x - prevPoint.x

        cubicTo(
            x1 = prevPoint.x + spikeDistance * bezierIntensity,
            y1 = prevY,
            x2 = curPoint.x - spikeDistance * bezierIntensity,
            y2 = curY,
            x3 = curPoint.x,
            y3 = curY,
        )
    }

    lineTo(x = plotPoints.last().x, y = startY)
    close()
}

private fun Float.getY(startY: Float, heightPx: Float): Float {
    return startY + this * heightPx / 2
}
