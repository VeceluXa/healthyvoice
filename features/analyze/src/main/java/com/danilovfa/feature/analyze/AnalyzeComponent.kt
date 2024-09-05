package com.danilovfa.feature.analyze

interface AnalyzeComponent {
    sealed class Output {
        data object NavigateBack : Output()
    }
}