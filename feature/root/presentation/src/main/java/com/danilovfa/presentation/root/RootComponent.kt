package com.danilovfa.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.danilovfa.presentation.cut.CutComponent
import com.danilovfa.presentation.record.RecordComponent
import com.danilovfa.presentation.analyze.AnalyzeComponent

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Record(val component: RecordComponent) : Child()
        data class Cut(val component: CutComponent) : Child()
        data class Analyze(val component: AnalyzeComponent) : Child()
    }
}