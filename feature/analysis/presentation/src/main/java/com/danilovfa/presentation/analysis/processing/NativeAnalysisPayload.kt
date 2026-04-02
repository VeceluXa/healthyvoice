package com.danilovfa.presentation.analysis.processing

import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi

internal data class NativeAnalysisPayload(
    val metrics: FloatArray,
    val totalNanos: Long,
    val stageLabels: Array<String>,
    val stageNanos: LongArray,
)

internal fun FloatArray.toAnalyzeParametersUi(): AnalyzeParametersUi {
    require(size >= METRIC_COUNT) { "Signal processor returned $size metrics, expected $METRIC_COUNT" }

    return AnalyzeParametersUi(
        j1 = getOrElse(0) { 0f },
        j3 = getOrElse(1) { 0f },
        j5 = getOrElse(2) { 0f },
        s1 = getOrElse(3) { 0f },
        s3 = getOrElse(4) { 0f },
        s5 = getOrElse(5) { 0f },
        s11 = getOrElse(6) { 0f },
        f0Mean = getOrElse(7) { 0f },
        f0Sd = getOrElse(8) { 0f },
    )
}

internal fun NativeAnalysisPayload.toTrace(): SignalProcessorTrace = SignalProcessorTrace(
    parameters = metrics.toAnalyzeParametersUi(),
    totalDurationMillis = totalNanos.toDurationMillis(),
    stageTimings = stageLabels.zip(stageNanos.asIterable()).map { (label, nanos) ->
        SignalProcessorStageTiming(
            label = label,
            durationMillis = nanos.toDurationMillis(),
        )
    },
)

internal fun Long.toDurationMillis(): Double = this / 1_000_000.0

private const val METRIC_COUNT = 9
