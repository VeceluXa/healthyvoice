package com.danilovfa.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.danilovfa.presentation.cut.CutComponent
import com.danilovfa.presentation.record.RecordComponent
import com.danilovfa.presentation.analysis.AnalyzeComponent
import com.danilovfa.presentation.patient.root.RootPatientComponent

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        internal data class Patient(val component: RootPatientComponent) : Child()
        internal data class Record(val component: RecordComponent) : Child()
        internal data class Cut(val component: CutComponent) : Child()
        internal data class Analyze(val component: AnalyzeComponent) : Child()
    }
}