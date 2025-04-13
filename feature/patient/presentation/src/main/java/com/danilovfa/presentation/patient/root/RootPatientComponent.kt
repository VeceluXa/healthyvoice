package com.danilovfa.presentation.patient.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.danilovfa.presentation.patient.create.PatientCreateComponent
import com.danilovfa.presentation.patient.detail.PatientDetailComponent
import com.danilovfa.presentation.patient.list.PatientListComponent

interface RootPatientComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Output {
        data class NavigateAnalysis(val recordingId: Long) : Output()
        data class NavigateRecord(val patientId: Long) : Output()
    }

    sealed class Child {
        internal data class List(val component: PatientListComponent) : Child()
        internal data class Detail(val component: PatientDetailComponent) : Child()
        internal data class Create(val component: PatientCreateComponent) : Child()
    }
}