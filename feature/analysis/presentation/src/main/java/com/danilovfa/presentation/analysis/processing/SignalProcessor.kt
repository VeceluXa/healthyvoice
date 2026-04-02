package com.danilovfa.presentation.analysis.processing

import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi

interface SignalProcessor {
    suspend fun analyze(samples: ShortArray, sampleRate: Int): AnalyzeParametersUi =
        analyzeDetailed(samples, sampleRate).parameters

    suspend fun analyzeDetailed(samples: ShortArray, sampleRate: Int): SignalProcessorTrace
}

data class SignalProcessorTrace(
    val parameters: AnalyzeParametersUi,
    val totalDurationMillis: Double,
    val stageTimings: List<SignalProcessorStageTiming> = emptyList(),
)

data class SignalProcessorStageTiming(
    val label: String,
    val durationMillis: Double,
)
