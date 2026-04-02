package com.danilovfa.presentation.analysis.processing

import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NativeSignalProcessor : SignalProcessor {
    override suspend fun analyze(samples: ShortArray, sampleRate: Int): AnalyzeParametersUi =
        withContext(Dispatchers.Default) {
            NativeSignalProcessorBridge.nativeAnalyze(samples, sampleRate).toAnalyzeParametersUi()
        }

    override suspend fun analyzeDetailed(
        samples: ShortArray,
        sampleRate: Int
    ): SignalProcessorTrace = withContext(Dispatchers.Default) {
        NativeSignalProcessorBridge.nativeAnalyzeDetailed(samples, sampleRate).toTrace()
    }
}
