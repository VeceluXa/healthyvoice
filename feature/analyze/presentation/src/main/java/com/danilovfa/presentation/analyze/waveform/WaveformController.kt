package com.danilovfa.presentation.analyze.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger

@JvmName("rememberWaveformControllerShort")
@Composable
internal fun rememberWaveformController(
    initialValues: List<Short>,
    onChunkSizeChanged: ((prev: Int, new: Int) -> Unit)? = null,
): WaveformController =
    rememberWaveformController(
        initialValues = WaveformController.normalizeValues(initialValues),
        onChunkSizeChanged = onChunkSizeChanged
    )

@Composable
internal fun rememberWaveformController(
    initialValues: List<Float>,
    onChunkSizeChanged: ((prev: Int, new: Int) -> Unit)? = null,
): WaveformController {
    return remember {
        WaveformController(
            initialValues = initialValues,
            onChunkSizeChanged = onChunkSizeChanged
        )
    }
}

class WaveformController(
    private val initialValues: List<Float>,
    val onChunkSizeChanged: ((prev: Int, new: Int) -> Unit)? = null,
) {
    private var currentChunkSize = 1

    var points by mutableStateOf(initialValues)
        private set

    fun scale(scale: Float) {
        val chunkSize = when (scale) {
            in 0.1f..0.5f -> 10
            in 0.05f..0.1f -> 100
            in 0.01f..0.05f -> 200
            else -> 1
        }

        Logger.withTag("Waveform").d("Amplitude Scale: $scale, Chunk Size: $chunkSize")

        if (currentChunkSize != chunkSize) {
            onChunkSizeChanged?.invoke(
                currentChunkSize,
                chunkSize
            )

            currentChunkSize = chunkSize
            points = initialValues
                .chunked(chunkSize)
                .map { it.average().toFloat() }

            Logger.withTag("Waveform").d("Amplitude Size: ${points.size}")
        }
    }

    companion object {
        @JvmName("fromShort")
        operator fun invoke(values: List<Short>) = WaveformController(normalizeValues(values))

        internal fun normalizeValues(values: List<Short>): List<Float> {
            return values.map {
                it.toFloat() / Short.MAX_VALUE
            }
        }
    }
}