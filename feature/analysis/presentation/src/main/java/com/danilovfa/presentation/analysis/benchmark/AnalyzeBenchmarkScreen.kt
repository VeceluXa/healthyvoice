package com.danilovfa.presentation.analysis.benchmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.button.OutlinedButtonLarge
import com.danilovfa.common.uikit.composables.state.LoaderStub
import com.danilovfa.common.uikit.composables.stub.StandardErrorStub
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.composables.toolbar.NavigationIcon
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.modifier.adaptiveMaxWidth
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.EngineStats
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.Intent
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.MetricComparison
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkComponent.State
import com.danilovfa.presentation.analysis.processing.SignalProcessorStageTiming

@Composable
fun AnalyzeBenchmarkScreen(component: AnalyzeBenchmarkComponent) {
    val state by component.stateFlow.collectAsState()

    AnalyzeBenchmarkLayout(
        state = state,
        onIntent = component::onIntent,
    )
}

@Composable
private fun AnalyzeBenchmarkLayout(
    state: State,
    onIntent: (Intent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .systemBarsPadding()
    ) {
        Toolbar(
            title = stringResource(strings.analyze_benchmark_title),
            navigationIcon = NavigationIcon.Back,
            onNavigationClick = { onIntent(Intent.OnBackClicked) },
            actions = {
                IconButton(onClick = { onIntent(Intent.OnRunBenchmarkClicked) }) {
                    Icon(
                        painter = AppIcon.Refresh,
                        tint = AppTheme.colors.primary,
                        contentDescription = "Run benchmark",
                    )
                }
            }
        )

        when {
            state.isRunning -> LoaderStub(
                text = stringResource(strings.analyze_benchmark_running),
                modifier = Modifier.weight(1f),
            )

            state.errorMessage != null -> StandardErrorStub(
                onRefreshClick = { onIntent(Intent.OnRunBenchmarkClicked) },
                modifier = Modifier.weight(1f),
            )

            else -> AnalyzeBenchmarkContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AnalyzeBenchmarkContent(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .adaptiveMaxWidth()
            .padding(horizontal = AppDimension.layoutHorizontalMargin),
        horizontalAlignment = Alignment.Start,
    ) {
        VSpacer(AppDimension.layoutMainMargin)

        state.sampleInfo?.let { sampleInfo ->
            InfoBlock(
                title = stringResource(strings.analyze_benchmark_sample),
                lines = listOf(
                    "${stringResource(strings.analyze_benchmark_sample_rate)}: ${sampleInfo.sampleRate}",
                    "${stringResource(strings.analyze_benchmark_sample_count)}: ${sampleInfo.sampleCount}",
                    "${stringResource(strings.analyze_benchmark_duration)}: ${sampleInfo.durationMillis} ms",
                )
            )
        }

        VSpacer(AppDimension.layoutLargeMargin)

        state.speedupRatio?.let { speedupRatio ->
            Text(
                text = stringResource(strings.analyze_benchmark_speedup, speedupRatio),
                style = AppTypography.titleMedium20,
            )
        }

        VSpacer(AppDimension.layoutMainMargin)

        state.nativeStats?.let {
            EngineStatsBlock(
                title = stringResource(strings.analyze_benchmark_native),
                stats = it,
            )
        }

        VSpacer(AppDimension.layoutMainMargin)

        state.pythonStats?.let {
            EngineStatsBlock(
                title = stringResource(strings.analyze_benchmark_python),
                stats = it,
            )
        }

        if (state.averagedNativeStageTimings.isNotEmpty()) {
            VSpacer(AppDimension.layoutLargeMargin)
            StageTimingsBlock(
                title = stringResource(strings.analyze_benchmark_native_stages),
                timings = state.averagedNativeStageTimings,
            )
        }

        if (state.metricsComparison.isNotEmpty()) {
            VSpacer(AppDimension.layoutLargeMargin)
            MetricsComparisonBlock(metrics = state.metricsComparison)
        }

        VSpacer(AppDimension.layoutLargeMargin)

        OutlinedButtonLarge(
            text = stringResource(strings.refresh),
            onClick = { onIntent(Intent.OnRunBenchmarkClicked) },
            modifier = Modifier.fillMaxWidth(),
        )

        VSpacer(AppDimension.layoutLargeMargin)
    }
}

@Composable
private fun InfoBlock(
    title: String,
    lines: List<String>,
) {
    Text(text = title, style = AppTypography.titleMedium20)
    VSpacer(AppDimension.layoutMediumMargin)
    lines.forEach { line ->
        Text(text = line, style = AppTypography.bodyRegular16)
        VSpacer(AppDimension.layoutSmallMargin)
    }
}

@Composable
private fun EngineStatsBlock(
    title: String,
    stats: EngineStats,
) {
    InfoBlock(
        title = title,
        lines = listOf(
            "${stringResource(strings.analyze_benchmark_warmup_runs)}: ${stats.warmupRuns}",
            "${stringResource(strings.analyze_benchmark_measured_runs)}: ${stats.measuredRuns}",
            "${stringResource(strings.analyze_benchmark_mean)}: ${stats.meanMillis.formatMillis()}",
            "${stringResource(strings.analyze_benchmark_median)}: ${stats.medianMillis.formatMillis()}",
            "${stringResource(strings.analyze_benchmark_min)}: ${stats.minMillis.formatMillis()}",
            "${stringResource(strings.analyze_benchmark_max)}: ${stats.maxMillis.formatMillis()}",
        )
    )
}

@Composable
private fun StageTimingsBlock(
    title: String,
    timings: List<SignalProcessorStageTiming>,
) {
    Text(text = title, style = AppTypography.titleMedium20)
    VSpacer(AppDimension.layoutMediumMargin)
    timings.forEach { timing ->
        Text(
            text = "${timing.label}: ${timing.durationMillis.formatMillis()}",
            style = AppTypography.bodyRegular16.copy(fontFamily = FontFamily.Monospace),
        )
        VSpacer(AppDimension.layoutSmallMargin)
    }
}

@Composable
private fun MetricsComparisonBlock(
    metrics: List<MetricComparison>,
) {
    Text(
        text = stringResource(strings.analyze_benchmark_parameter_delta),
        style = AppTypography.titleMedium20,
    )
    VSpacer(AppDimension.layoutMediumMargin)

    metrics.forEach { metric ->
        Column {
            Text(text = metric.label, style = AppTypography.bodyMedium16)
            VSpacer(AppDimension.layoutSmallMargin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(strings.analyze_benchmark_native_value, metric.nativeValue),
                    style = AppTypography.bodyRegular16,
                )
                Text(
                    text = stringResource(strings.analyze_benchmark_python_value, metric.pythonValue),
                    style = AppTypography.bodyRegular16,
                )
            }
            VSpacer(AppDimension.layoutSmallMargin)
            Text(
                text = stringResource(strings.analyze_benchmark_delta_value, metric.absoluteDelta),
                style = AppTypography.bodyRegular16.copy(fontFamily = FontFamily.Monospace),
            )
        }
        VSpacer(AppDimension.layoutMainMargin)
    }
}

private fun Double.formatMillis(): String = String.format("%.2f ms", this)

@Composable
@Preview
private fun AnalyzeBenchmarkPreview() {
    AppTheme {
        AnalyzeBenchmarkLayout(
            state = State(
                isRunning = false,
                sampleInfo = AnalyzeBenchmarkComponent.SampleInfo(
                    sampleRate = 44_100,
                    sampleCount = 132_300,
                    durationMillis = 3_000,
                ),
                nativeStats = EngineStats(
                    warmupRuns = 3,
                    measuredRuns = 10,
                    meanMillis = 15.2,
                    medianMillis = 14.8,
                    minMillis = 13.9,
                    maxMillis = 17.1,
                    parameters = com.danilovfa.presentation.analysis.model.AnalyzeParametersUi(
                        j1 = 0.2f,
                        j3 = 0.3f,
                        j5 = 0.4f,
                        s1 = 1.2f,
                        s3 = 1.3f,
                        s5 = 1.4f,
                        s11 = 1.5f,
                        f0Mean = 128.2f,
                        f0Sd = 4.1f,
                    ),
                ),
                pythonStats = EngineStats(
                    warmupRuns = 1,
                    measuredRuns = 10,
                    meanMillis = 20.1,
                    medianMillis = 19.7,
                    minMillis = 18.9,
                    maxMillis = 22.4,
                    parameters = com.danilovfa.presentation.analysis.model.AnalyzeParametersUi(
                        j1 = 0.19f,
                        j3 = 0.31f,
                        j5 = 0.41f,
                        s1 = 1.22f,
                        s3 = 1.29f,
                        s5 = 1.41f,
                        s11 = 1.49f,
                        f0Mean = 128.0f,
                        f0Sd = 4.2f,
                    ),
                ),
                averagedNativeStageTimings = listOf(
                    SignalProcessorStageTiming("voice_segmentation", 4.0),
                    SignalProcessorStageTiming("wm_method", 8.6),
                    SignalProcessorStageTiming("voice_parameters", 2.1),
                ),
                metricsComparison = listOf(
                    MetricComparison("Jitter:loc [%]", 0.2f, 0.19f, 0.01f),
                    MetricComparison("F0 [Hz]", 128.2f, 128.0f, 0.2f),
                ),
                speedupRatio = 1.33,
            ),
            onIntent = {},
        )
    }
}
