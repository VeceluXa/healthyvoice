package com.danilovfa.presentation.analysis.benchmark

import androidx.compose.runtime.Immutable
import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import com.danilovfa.presentation.analysis.processing.SignalProcessorStageTiming

interface AnalyzeBenchmarkComponent : StatefulComponent<AnalyzeBenchmarkComponent.Intent, AnalyzeBenchmarkComponent.State> {
    sealed class Intent {
        data object OnBackClicked : Intent()
        data object OnRunBenchmarkClicked : Intent()
    }

    @Immutable
    data class State(
        val isRunning: Boolean = true,
        val sampleInfo: SampleInfo? = null,
        val nativeStats: EngineStats? = null,
        val pythonStats: EngineStats? = null,
        val averagedNativeStageTimings: List<SignalProcessorStageTiming> = emptyList(),
        val metricsComparison: List<MetricComparison> = emptyList(),
        val speedupRatio: Double? = null,
        val errorMessage: String? = null,
    )

    data class SampleInfo(
        val sampleRate: Int,
        val sampleCount: Int,
        val durationMillis: Int,
    )

    data class EngineStats(
        val warmupRuns: Int,
        val measuredRuns: Int,
        val meanMillis: Double,
        val medianMillis: Double,
        val minMillis: Double,
        val maxMillis: Double,
        val parameters: AnalyzeParametersUi,
    )

    data class MetricComparison(
        val label: String,
        val nativeValue: Float,
        val pythonValue: Float,
        val absoluteDelta: Float,
    )

    sealed class Output {
        data object NavigateBack : Output()
    }
}
