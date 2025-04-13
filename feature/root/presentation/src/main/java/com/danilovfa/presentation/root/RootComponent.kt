package com.danilovfa.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.danilovfa.presentation.analysis.AnalyzeComponent
import com.danilovfa.presentation.patient.root.RootPatientComponent
import com.danilovfa.presentation.record.root.RecordRootComponent

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        internal data class Patient(val component: RootPatientComponent) : Child()
        internal data class Recording(val component: RecordRootComponent) : Child()
        internal data class Analyze(val component: AnalyzeComponent) : Child()
    }
}