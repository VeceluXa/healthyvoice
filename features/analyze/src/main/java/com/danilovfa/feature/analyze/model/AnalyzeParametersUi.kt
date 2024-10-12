package com.danilovfa.feature.analyze.model

data class AnalyzeParametersUi(
    val j1: Float,
    val j3: Float,
    val j5: Float,
    val s1: Float,
    val s3: Float,
    val s5: Float,
    val s11: Float,
    val f0Mean: Float,
    val f0Sd: Float
)

internal fun AnalyzeParametersUi.toParametersData(): List<ParameterDataUi> = listOf(
    ParameterDataUi(
        label = "Jitter:loc [%]",
        value = j1,
        normMin = 1.1f,
        normMax = 7.3f
    ),
    ParameterDataUi(
        label = "Jitter:rap [%]",
        value = j3,
        normMin = 1.1f,
        normMax = 7.3f
    ),
    ParameterDataUi(
        label = "Jitter:ppq5 [%]",
        value = j5,
        normMin = 1.1f,
        normMax = 7.3f
    ),
    ParameterDataUi(
        label = "Shimmer:loc [%]",
        value = s1,
        normMin = 1.1f,
        normMax = 7.3f
    ),
    ParameterDataUi(
        label = "Shimmer:apq3 [%]",
        value = s3,
        normMin = 0.9f,
        normMax = 5.3f
    ),
    ParameterDataUi(
        label = "Shimmer:apq5 [%]",
        value = s5,
        normMin = 1.1f,
        normMax = 7.3f
    ),
    ParameterDataUi(
        label = "Shimmer:apq11 [%]",
        value = s11,
        normMin = 1.1f,
        normMax = 2.9f
    ),
    ParameterDataUi(
        label = "DPF [%]",
        value = f0Mean,
        normMin = 0.1f,
        normMax = 4.2f
    ),
    ParameterDataUi(
        label = "DF F0 [Hz]",
        value = f0Sd,
        normMin = 5.1f,
        normMax = 20.9f
    )
)
