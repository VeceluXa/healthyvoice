package com.danilovfa.presentation.record.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.danilovfa.presentation.record.cut.CutComponent
import com.danilovfa.presentation.record.main.RecordComponent

interface RecordRootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Output {
        data object NavigateBack : Output()
        data class NavigateAnalysis(val recordingId: Long) : Output()
    }

    sealed class Child {
        internal data class Record(val component: RecordComponent) : Child()
        internal data class Cut(val component: CutComponent) : Child()
    }
}