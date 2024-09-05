package com.danilovfa.feature.record

interface RecordComponent {
    sealed class Output {
        data class Analyze(val filename: String) : Output()
    }
}