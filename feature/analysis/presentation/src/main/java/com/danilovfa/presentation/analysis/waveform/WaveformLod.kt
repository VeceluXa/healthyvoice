package com.danilovfa.presentation.analysis.waveform

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

private const val OverscanViewportFractionPerSide = 0.5f
private const val TargetBucketWidthPx = 1f
private const val EmptyAmplitudePlaceholder = 0f

internal data class WaveformViewport(
    val contentWidthPx: Float,
    val spikeDistancePx: Float,
    val visibleStartIndex: Int,
    val visibleEndIndex: Int,
    val renderStartIndex: Int,
    val renderEndIndex: Int,
    val overscanPoints: Int,
    val scrollDelta: Float,
)

internal data class WaveformPlotPoint(
    val x: Float,
    val amplitude: Float,
)

internal fun calculateWaveformViewport(
    pointCount: Int,
    canvasWidthPx: Float,
    scrollDelta: Float,
    contentWidthPx: Float,
    overscanViewportFraction: Float = OverscanViewportFractionPerSide,
): WaveformViewport? {
    if (pointCount <= 0 || canvasWidthPx <= 0f || contentWidthPx <= 0f) {
        return null
    }

    val spikeDistancePx = contentWidthPx / pointCount
    if (!spikeDistancePx.isFinite() || spikeDistancePx <= 0f) {
        return null
    }

    val lastIndex = pointCount - 1
    val visibleStartIndex = floor((-scrollDelta) / spikeDistancePx).toInt().coerceIn(0, lastIndex)
    val visibleEndIndex = ceil((-scrollDelta + canvasWidthPx) / spikeDistancePx)
        .toInt()
        .coerceIn(0, lastIndex)
    val overscanPoints = ceil(canvasWidthPx * overscanViewportFraction / spikeDistancePx)
        .toInt()
        .coerceAtLeast(1)

    return WaveformViewport(
        contentWidthPx = contentWidthPx,
        spikeDistancePx = spikeDistancePx,
        visibleStartIndex = visibleStartIndex,
        visibleEndIndex = visibleEndIndex,
        renderStartIndex = (visibleStartIndex - overscanPoints).coerceAtLeast(0),
        renderEndIndex = (visibleEndIndex + overscanPoints).coerceAtMost(lastIndex),
        overscanPoints = overscanPoints,
        scrollDelta = scrollDelta,
    )
}

internal fun buildWaveformPlotPoints(
    amplitudes: List<Float>,
    viewport: WaveformViewport,
): List<WaveformPlotPoint> {
    if (viewport.renderStartIndex > viewport.renderEndIndex) {
        return emptyList()
    }

    return if (viewport.spikeDistancePx >= TargetBucketWidthPx) {
        buildRawPlotPoints(amplitudes = amplitudes, viewport = viewport)
    } else {
        buildPeakPreservingPlotPoints(amplitudes = amplitudes, viewport = viewport)
    }
}

internal fun selectPeakAmplitude(
    amplitudes: List<Float>,
    startIndexInclusive: Int,
    endIndexExclusive: Int,
): Float {
    if (startIndexInclusive !in amplitudes.indices || startIndexInclusive >= endIndexExclusive) {
        return EmptyAmplitudePlaceholder
    }

    val safeEndExclusive = endIndexExclusive.coerceAtMost(amplitudes.size)
    var selectedAmplitude = amplitudes[startIndexInclusive]
    var selectedAbsAmplitude = abs(selectedAmplitude)

    for (index in (startIndexInclusive + 1) until safeEndExclusive) {
        val amplitude = amplitudes[index]
        val amplitudeAbs = abs(amplitude)
        if (amplitudeAbs > selectedAbsAmplitude) {
            selectedAmplitude = amplitude
            selectedAbsAmplitude = amplitudeAbs
        }
    }

    return selectedAmplitude
}

internal fun calculateCutLineOffset(
    cutMillis: Int?,
    durationMillis: Int,
    contentWidthPx: Float,
    scrollDelta: Float,
): Float? {
    if (cutMillis == null || durationMillis <= 0) {
        return null
    }

    return cutMillis.toFloat() / durationMillis * contentWidthPx + scrollDelta
}

private fun buildRawPlotPoints(
    amplitudes: List<Float>,
    viewport: WaveformViewport,
): List<WaveformPlotPoint> {
    val plotPoints = ArrayList<WaveformPlotPoint>(viewport.renderEndIndex - viewport.renderStartIndex + 1)
    for (index in viewport.renderStartIndex..viewport.renderEndIndex) {
        plotPoints += WaveformPlotPoint(
            x = viewport.scrollDelta + index * viewport.spikeDistancePx,
            amplitude = amplitudes[index],
        )
    }
    return plotPoints
}

private fun buildPeakPreservingPlotPoints(
    amplitudes: List<Float>,
    viewport: WaveformViewport,
): List<WaveformPlotPoint> {
    val estimatedBucketCount = ceil(
        ((viewport.renderEndIndex - viewport.renderStartIndex + 1) * viewport.spikeDistancePx) /
            TargetBucketWidthPx
    ).toInt().coerceAtLeast(1)
    val plotPoints = ArrayList<WaveformPlotPoint>(estimatedBucketCount)

    var bucketStartIndex = viewport.renderStartIndex
    var currentBucket = bucketForIndex(
        index = bucketStartIndex,
        spikeDistancePx = viewport.spikeDistancePx,
    )

    for (index in (viewport.renderStartIndex + 1)..viewport.renderEndIndex) {
        val bucket = bucketForIndex(
            index = index,
            spikeDistancePx = viewport.spikeDistancePx,
        )
        if (bucket != currentBucket) {
            plotPoints += WaveformPlotPoint(
                x = viewport.scrollDelta + bucketCenterX(currentBucket),
                amplitude = selectPeakAmplitude(
                    amplitudes = amplitudes,
                    startIndexInclusive = bucketStartIndex,
                    endIndexExclusive = index,
                ),
            )
            bucketStartIndex = index
            currentBucket = bucket
        }
    }

    plotPoints += WaveformPlotPoint(
        x = viewport.scrollDelta + bucketCenterX(currentBucket),
        amplitude = selectPeakAmplitude(
            amplitudes = amplitudes,
            startIndexInclusive = bucketStartIndex,
            endIndexExclusive = viewport.renderEndIndex + 1,
        ),
    )

    return plotPoints
}

private fun bucketForIndex(
    index: Int,
    spikeDistancePx: Float,
): Int = floor(index * spikeDistancePx / TargetBucketWidthPx).toInt()

private fun bucketCenterX(bucket: Int): Float = (bucket + 0.5f) * TargetBucketWidthPx
