package com.danilovfa.healthyvoice

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danilovfa.libs.recorder.recorder.wav.WavPcmReader
import com.danilovfa.presentation.analysis.benchmark.AnalyzeBenchmarkConfig
import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import com.danilovfa.presentation.analysis.processing.NativeSignalProcessor
import com.danilovfa.presentation.analysis.processing.PythonSignalProcessorReference
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignalProcessorParityTest {
    @Test
    fun nativeProcessorMatchesPythonReferenceForBenchmarkAsset() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val decoded = context.assets.open(AnalyzeBenchmarkConfig.BenchmarkAssetPath).use(WavPcmReader::read)

        val nativeProcessor = NativeSignalProcessor()
        val pythonProcessor = PythonSignalProcessorReference(context)

        val native = nativeProcessor.analyze(decoded.samples, decoded.config.frequency)
        val python = pythonProcessor.analyze(decoded.samples, decoded.config.frequency)

        assertNoNaNs(native)
        assertNoNaNs(python)

        assertWithinTolerance(native.j1, python.j1, AnalyzeBenchmarkConfig.PerturbationTolerance, "j1")
        assertWithinTolerance(native.j3, python.j3, AnalyzeBenchmarkConfig.PerturbationTolerance, "j3")
        assertWithinTolerance(native.j5, python.j5, AnalyzeBenchmarkConfig.PerturbationTolerance, "j5")
        assertWithinTolerance(native.s1, python.s1, AnalyzeBenchmarkConfig.PerturbationTolerance, "s1")
        assertWithinTolerance(native.s3, python.s3, AnalyzeBenchmarkConfig.PerturbationTolerance, "s3")
        assertWithinTolerance(native.s5, python.s5, AnalyzeBenchmarkConfig.PerturbationTolerance, "s5")
        assertWithinTolerance(native.s11, python.s11, AnalyzeBenchmarkConfig.PerturbationTolerance, "s11")
        assertWithinTolerance(native.f0Mean, python.f0Mean, AnalyzeBenchmarkConfig.FrequencyToleranceHz, "f0Mean")
        assertWithinTolerance(native.f0Sd, python.f0Sd, AnalyzeBenchmarkConfig.FrequencyToleranceHz, "f0Sd")
    }

    private fun assertWithinTolerance(actual: Float, expected: Float, tolerance: Float, label: String) {
        val delta = kotlin.math.abs(actual - expected)
        assertFalse("$label delta=$delta tolerance=$tolerance", delta > tolerance)
    }

    private fun assertNoNaNs(parameters: AnalyzeParametersUi) {
        assertFalse(parameters.j1.isNaN())
        assertFalse(parameters.j3.isNaN())
        assertFalse(parameters.j5.isNaN())
        assertFalse(parameters.s1.isNaN())
        assertFalse(parameters.s3.isNaN())
        assertFalse(parameters.s5.isNaN())
        assertFalse(parameters.s11.isNaN())
        assertFalse(parameters.f0Mean.isNaN())
        assertFalse(parameters.f0Sd.isNaN())
    }
}
