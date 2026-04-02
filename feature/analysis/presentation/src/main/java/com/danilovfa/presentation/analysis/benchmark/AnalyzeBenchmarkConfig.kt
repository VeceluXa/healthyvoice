package com.danilovfa.presentation.analysis.benchmark

object AnalyzeBenchmarkConfig {
    const val BenchmarkAssetPath = "benchmarks/117_a.wav"

    const val NativeWarmupRuns = 3
    const val PythonWarmupRuns = 1
    const val MeasuredRuns = 10

    const val PerturbationTolerance = 0.05f
    const val FrequencyToleranceHz = 0.5f
}
