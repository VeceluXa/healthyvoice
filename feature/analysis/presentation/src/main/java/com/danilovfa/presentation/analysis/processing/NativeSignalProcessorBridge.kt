package com.danilovfa.presentation.analysis.processing

internal object NativeSignalProcessorBridge {
    init {
        System.loadLibrary("signal_processor_native")
    }

    external fun nativeAnalyze(samples: ShortArray, sampleRate: Int): FloatArray

    external fun nativeAnalyzeDetailed(samples: ShortArray, sampleRate: Int): NativeAnalysisPayload
}
