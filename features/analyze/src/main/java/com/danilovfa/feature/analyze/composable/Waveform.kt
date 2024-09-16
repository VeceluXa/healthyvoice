package com.danilovfa.feature.analyze.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.danilovfa.uikit.theme.AppTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

internal const val WAVEFORM_HEIGHT_DP = 300
private const val SPACE_BETWEEN_DOTS = 5

private const val EmptyAmplitudePlaceholder = 0f
private const val OffscreenSpikesMultiplier = 2
private const val BezierPerformanceLimit = 10

@Composable
internal fun Waveform(
    amplitudes: WaveformAmplitudes,
    modifier: Modifier = Modifier,
    brush: Brush = SolidColor(AppTheme.colors.buttonSecondary),
    bezierIntensity: Float = 0.35f,
) {
    val density = LocalDensity.current
    val screenWidthPx = with (density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    var minScrollDelta by remember {
        mutableStateOf(10000f)
    }

    var scrollDelta by remember {
        mutableStateOf(-(screenWidthPx / 2))
    }

    val waveformHeightPx = with(LocalDensity.current) {
        WAVEFORM_HEIGHT_DP.dp.toPx()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(WAVEFORM_HEIGHT_DP.dp)
            .pointerInput(Unit) {

            }
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            val spikeDistance = abs(minScrollDelta) / amplitudes.values.size
            val spikesOnScreen = ceil(size.width / spikeDistance).toInt()
            val sideSpikesCount = ceil(spikesOnScreen / 2f).toInt()

            val middleSpikeIndex = floor(-scrollDelta / spikeDistance).toInt()
            val minVisibleSpikeIndex = middleSpikeIndex - sideSpikesCount
            val maxVisibleSpikeIndex =
                middleSpikeIndex + (sideSpikesCount * OffscreenSpikesMultiplier)
            val spikesIndices = minVisibleSpikeIndex..maxVisibleSpikeIndex

            val startX = 0f//scrollDelta % spikeDistance
            val path = Path().apply {
                if (spikesOnScreen > BezierPerformanceLimit) waveform(
                    amplitudes = amplitudes.values,
                    visibleSpikesIndexes = spikesIndices,
                    spikeDistance = spikeDistance,
                    startX = startX,
                    startY = center.y,
                    waveformHeightPx = waveformHeightPx
                ) else bezierInterpolatedWaveform(
                    amplitudes = amplitudes.values,
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
                brush = brush,
            )
//            flip(FlipDirection.Vertical) {
//                drawPath(
//                    path = path,
//                    brush = brush,
//                )
//            }
            drawLine(
                brush = brush,
                start = Offset(0f, size.center.y),
                end = Offset(size.width, size.center.y),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
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


data class WaveformAmplitudes(
    val values: List<Float>
) {

    companion object {
        @JvmName("fromShort")
        operator fun invoke(values: List<Short>) = WaveformAmplitudes(normalizeValues(values))

        private fun normalizeValues(values: List<Short>): List<Float> {
            return values.map {
                it.toFloat() / Short.MAX_VALUE
            }
        }
    }

}