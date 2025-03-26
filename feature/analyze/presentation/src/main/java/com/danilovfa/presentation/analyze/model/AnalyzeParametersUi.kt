package com.danilovfa.presentation.analyze.model

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
        normMin = 0.10f,
        normMax = 1.61f
    ),
    ParameterDataUi(
        label = "Jitter:rap [%]",
        value = j3,
        normMin = 0.07f,
        normMax = 0.99f
    ),
    ParameterDataUi(
        label = "Jitter:ppq5 [%]",
        value = j5,
        normMin = 0.09f,
        normMax = 0.89f
    ),
    ParameterDataUi(
        label = "Shimmer:loc [%]",
        value = s1,
        normMin = 0.88f,
        normMax = 11.22f
    ),
    ParameterDataUi(
        label = "Shimmer:apq3 [%]",
        value = s3,
        normMin = 0.45f,
        normMax = 5.71f
    ),
    ParameterDataUi(
        label = "Shimmer:apq5 [%]",
        value = s5,
        normMin = 0.50f,
        normMax = 6.99f
    ),
    ParameterDataUi(
        label = "Shimmer:apq11 [%]",
        value = s11,
        normMin = 0.80f,
        normMax = 11.71f
    ),
    ParameterDataUi(
        label = "F0 [Hz]",
        value = f0Mean,
        normMin = null,
        normMax = null
    ),
    ParameterDataUi(
        label = "SD F0 [Hz]",
        value = f0Sd,
        normMin = null,
        normMax = null
    )
)
