package com.danilovfa.presentation.analysis.benchmark

import android.content.Context
import com.danilovfa.common.base.component.BaseDefaultComponent
import com.danilovfa.libs.recorder.recorder.wav.WavPcmReader
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.EngineStats
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.Intent
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.MetricComparison
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.Output
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.SampleInfo
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.State
import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import com.danilovfa.presentation.analysis.processing.NativeSignalProcessor
import com.danilovfa.presentation.analysis.processing.PythonSignalProcessorReference
import com.danilovfa.presentation.analysis.processing.SignalProcessor
import com.danilovfa.presentation.analysis.processing.SignalProcessorStageTiming
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.abs

class DefaultAnalyzeBenchmarkComponent(
    componentContext: ComponentContext,
    private val output: (Output) -> Unit,
) : AnalyzeBenchmarkComponent, BaseDefaultComponent(componentContext), KoinComponent {
    private val context: Context by inject()
    private val nativeSignalProcessor = NativeSignalProcessor()
    private val pythonSignalProcessor by lazy { PythonSignalProcessorReference(context) }

    private val mutableStateFlow = MutableStateFlow(State())
    private var sample: BenchmarkSample? = null

    override val stateFlow: StateFlow<State> = mutableStateFlow.asStateFlow()

    init {
        runBenchmark()
    }

    override fun onIntent(intent: Intent) = when (intent) {
        Intent.OnBackClicked -> output(Output.NavigateBack)
        Intent.OnRunBenchmarkClicked -> runBenchmark()
    }

    private fun runBenchmark() {
        scope.launch {
            mutableStateFlow.value = mutableStateFlow.value.copy(
                isRunning = true,
                errorMessage = null,
            )

            runCatching {
                val loadedSample = sample ?: loadSample().also { sample = it }
                val nativeResult = benchmarkEngine(
                    processor = nativeSignalProcessor,
                    warmupRuns = AnalyzeBenchmarkConfig.NativeWarmupRuns,
                    measuredRuns = AnalyzeBenchmarkConfig.MeasuredRuns,
                    sample = loadedSample,
                )
                val pythonResult = benchmarkEngine(
                    processor = pythonSignalProcessor,
                    warmupRuns = AnalyzeBenchmarkConfig.PythonWarmupRuns,
                    measuredRuns = AnalyzeBenchmarkConfig.MeasuredRuns,
                    sample = loadedSample,
                )

                mutableStateFlow.value = State(
                    isRunning = false,
                    sampleInfo = loadedSample.info,
                    nativeStats = nativeResult.stats,
                    pythonStats = pythonResult.stats,
                    averagedNativeStageTimings = nativeResult.averageStageTimings,
                    metricsComparison = compareMetrics(
                        native = nativeResult.stats.parameters,
                        python = pythonResult.stats.parameters,
                    ),
                    speedupRatio = if (nativeResult.stats.medianMillis == 0.0) {
                        null
                    } else {
                        pythonResult.stats.medianMillis / nativeResult.stats.medianMillis
                    },
                )
            }.onFailure { error ->
                mutableStateFlow.value = mutableStateFlow.value.copy(
                    isRunning = false,
                    errorMessage = error.message ?: "Benchmark failed",
                )
            }
        }
    }

    private suspend fun loadSample(): BenchmarkSample = withContext(Dispatchers.IO) {
        context.assets.open(AnalyzeBenchmarkConfig.BenchmarkAssetPath).use { input ->
            val decoded = WavPcmReader.read(input)
            BenchmarkSample(
                samples = decoded.samples,
                info = SampleInfo(
                    sampleRate = decoded.config.frequency,
                    sampleCount = decoded.samples.size,
                    durationMillis = decoded.durationMillis,
                ),
            )
        }
    }

    private suspend fun benchmarkEngine(
        processor: SignalProcessor,
        warmupRuns: Int,
        measuredRuns: Int,
        sample: BenchmarkSample,
    ): BenchmarkResult {
        repeat(warmupRuns) {
            processor.analyze(sample.samples, sample.info.sampleRate)
        }

        val traces = buildList {
            repeat(measuredRuns) {
                add(processor.analyzeDetailed(sample.samples, sample.info.sampleRate))
            }
        }

        val durations = traces.map { it.totalDurationMillis }.sorted()
        val meanMillis = durations.average()
        val medianMillis = when {
            durations.isEmpty() -> 0.0
            durations.size % 2 == 0 -> {
                val upper = durations.size / 2
                (durations[upper - 1] + durations[upper]) / 2.0
            }
            else -> durations[durations.size / 2]
        }
        val parameters = traces.last().parameters
        val averageStageTimings = traces
            .flatMap { it.stageTimings }
            .groupBy { it.label }
            .map { (label, timings) ->
                SignalProcessorStageTiming(
                    label = label,
                    durationMillis = timings.map { it.durationMillis }.average(),
                )
            }
            .sortedBy { it.label }

        return BenchmarkResult(
            stats = EngineStats(
                warmupRuns = warmupRuns,
                measuredRuns = measuredRuns,
                meanMillis = meanMillis,
                medianMillis = medianMillis,
                minMillis = durations.minOrNull() ?: 0.0,
                maxMillis = durations.maxOrNull() ?: 0.0,
                parameters = parameters,
            ),
            averageStageTimings = averageStageTimings,
        )
    }

    private fun compareMetrics(
        native: AnalyzeParametersUi,
        python: AnalyzeParametersUi,
    ): List<MetricComparison> = listOf(
        MetricComparison("Jitter:loc [%]", native.j1, python.j1, abs(native.j1 - python.j1)),
        MetricComparison("Jitter:rap [%]", native.j3, python.j3, abs(native.j3 - python.j3)),
        MetricComparison("Jitter:ppq5 [%]", native.j5, python.j5, abs(native.j5 - python.j5)),
        MetricComparison("Shimmer:loc [%]", native.s1, python.s1, abs(native.s1 - python.s1)),
        MetricComparison("Shimmer:apq3 [%]", native.s3, python.s3, abs(native.s3 - python.s3)),
        MetricComparison("Shimmer:apq5 [%]", native.s5, python.s5, abs(native.s5 - python.s5)),
        MetricComparison("Shimmer:apq11 [%]", native.s11, python.s11, abs(native.s11 - python.s11)),
        MetricComparison("F0 [Hz]", native.f0Mean, python.f0Mean, abs(native.f0Mean - python.f0Mean)),
        MetricComparison("SD F0 [Hz]", native.f0Sd, python.f0Sd, abs(native.f0Sd - python.f0Sd)),
    )

    private data class BenchmarkSample(
        val samples: ShortArray,
        val info: SampleInfo,
    )

    private data class BenchmarkResult(
        val stats: EngineStats,
        val averageStageTimings: List<SignalProcessorStageTiming>,
    )
}
