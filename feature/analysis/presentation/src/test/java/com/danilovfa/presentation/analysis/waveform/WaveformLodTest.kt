package com.danilovfa.presentation.analysis.waveform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WaveformLodTest {

    @Test
    fun `selectPeakAmplitude preserves strongest sample sign`() {
        val amplitude = selectPeakAmplitude(
            amplitudes = listOf(-0.2f, 0.5f, -0.8f, 0.4f),
            startIndexInclusive = 0,
            endIndexExclusive = 4,
        )

        assertEquals(-0.8f, amplitude)
    }

    @Test
    fun `selectPeakAmplitude returns placeholder for empty range`() {
        val amplitude = selectPeakAmplitude(
            amplitudes = listOf(0.4f),
            startIndexInclusive = 1,
            endIndexExclusive = 1,
        )

        assertEquals(0f, amplitude)
    }

    @Test
    fun `buildWaveformPlotPoints keeps raw points when spike distance is at least one pixel`() {
        val amplitudes = listOf(0.1f, -0.2f, 0.3f, -0.4f)
        val viewport = calculateWaveformViewport(
            pointCount = amplitudes.size,
            canvasWidthPx = 4f,
            scrollDelta = 0f,
            contentWidthPx = 4f,
        )!!

        val plotPoints = buildWaveformPlotPoints(
            amplitudes = amplitudes,
            viewport = viewport,
        )

        assertEquals(amplitudes.size, plotPoints.size)
        assertEquals(listOf(0f, 1f, 2f, 3f), plotPoints.map { it.x })
        assertEquals(amplitudes, plotPoints.map { it.amplitude })
    }

    @Test
    fun `zoomed out plot stays under target density`() {
        val amplitudes = List(40_000) { index ->
            if (index % 3 == 0) -0.9f else 0.25f
        }
        val viewport = calculateWaveformViewport(
            pointCount = amplitudes.size,
            canvasWidthPx = 1080f,
            scrollDelta = 0f,
            contentWidthPx = 1080f,
        )!!

        val plotPoints = buildWaveformPlotPoints(
            amplitudes = amplitudes,
            viewport = viewport,
        )

        assertTrue(plotPoints.size <= 2160)
    }

    @Test
    fun `viewport adds half-screen overscan on each side`() {
        val viewport = calculateWaveformViewport(
            pointCount = 4_000,
            canvasWidthPx = 1_000f,
            scrollDelta = -1_000f,
            contentWidthPx = 4_000f,
        )!!

        assertEquals(1_000, viewport.visibleStartIndex)
        assertEquals(2_000, viewport.visibleEndIndex)
        assertEquals(500, viewport.overscanPoints)
        assertEquals(500, viewport.renderStartIndex)
        assertEquals(2_500, viewport.renderEndIndex)
    }

    @Test
    fun `cut line offset uses content width`() {
        val offset = calculateCutLineOffset(
            cutMillis = 500,
            durationMillis = 1_000,
            contentWidthPx = 40_000f,
            scrollDelta = -200f,
        )

        assertEquals(19_800f, offset)
    }
}
