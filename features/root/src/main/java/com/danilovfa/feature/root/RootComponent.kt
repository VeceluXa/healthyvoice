package com.danilovfa.feature.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.danilovfa.feature.analyze.AnalyzeComponent
import com.danilovfa.feature.record.RecordComponent

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Record(val component: RecordComponent) : Child()
        data class Analyze(val component: AnalyzeComponent) : Child()
    }
}