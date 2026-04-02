package com.danilovfa.presentation.analysis.processing

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PythonSignalProcessorReference(
    private val context: Context,
) : SignalProcessor {
    override suspend fun analyzeDetailed(
        samples: ShortArray,
        sampleRate: Int
    ): SignalProcessorTrace = withContext(Dispatchers.Default) {
        ensurePythonStarted()

        val analyzer = Python.getInstance().getModule("signal_processor")
        val rawSamples = samples.toTypedArray()
        val stageTimings = mutableListOf<SignalProcessorStageTiming>()
        val totalStartedAt = System.nanoTime()

        val voiceSegmentationStartedAt = System.nanoTime()
        val pySegments = analyzer.callAttr(
            "voice_segmentation",
            rawSamples,
            sampleRate
        ).asList()
        stageTimings += SignalProcessorStageTiming(
            label = "voice_segmentation",
            durationMillis = (System.nanoTime() - voiceSegmentationStartedAt).toDurationMillis(),
        )

        val wmMethodStartedAt = System.nanoTime()
        val pyWmMethod = analyzer.callAttr(
            "WM_method",
            rawSamples,
            pySegments[1],
            sampleRate,
            pySegments[0]
        ).asList()
        stageTimings += SignalProcessorStageTiming(
            label = "wm_method",
            durationMillis = (System.nanoTime() - wmMethodStartedAt).toDurationMillis(),
        )

        val voiceParametersStartedAt = System.nanoTime()
        val pyParams = analyzer.callAttr(
            "voice_parameters",
            rawSamples,
            pyWmMethod[1],
            pyWmMethod[0]
        )
        stageTimings += SignalProcessorStageTiming(
            label = "voice_parameters",
            durationMillis = (System.nanoTime() - voiceParametersStartedAt).toDurationMillis(),
        )

        SignalProcessorTrace(
            parameters = pyParams.asList().map { it.toFloat() }.toFloatArray().toAnalyzeParametersUi(),
            totalDurationMillis = (System.nanoTime() - totalStartedAt).toDurationMillis(),
            stageTimings = stageTimings,
        )
    }

    private fun ensurePythonStarted() {
        if (Python.isStarted().not()) {
            Python.start(AndroidPlatform(context.applicationContext))
        }
    }
}
