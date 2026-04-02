package com.danilovfa.presentation.analysis.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@JvmName("rememberWaveformControllerShort")
@Composable
internal fun rememberWaveformController(initialValues: List<Short>): WaveformController =
    remember(initialValues) {
        WaveformController(points = WaveformController.normalizeValues(initialValues))
    }

@Composable
internal fun rememberWaveformController(initialValues: List<Float>): WaveformController =
    remember(initialValues) {
        WaveformController(points = initialValues)
    }

internal class WaveformController(
    val points: List<Float>,
) {
    companion object {
        @JvmName("fromShort")
        operator fun invoke(values: List<Short>) = WaveformController(points = normalizeValues(values))

        internal fun normalizeValues(values: List<Short>): List<Float> {
            return values.map {
                it.toFloat() / Short.MAX_VALUE
            }
        }
    }
}
