package com.danilovfa.presentation.patient.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.danilovfa.presentation.patient.create.PatientCreateScreen
import com.danilovfa.presentation.patient.detail.PatientDetailScreen
import com.danilovfa.presentation.patient.list.PatientListScreen
import com.danilovfa.presentation.patient.root.RootPatientComponent.Child

@Composable
fun RootPatientScreen(component: RootPatientComponent) {
    val childStack by component.childStack.subscribeAsState()

    Children(childStack) {
        when (val child = childStack.active.instance) {
            is Child.Create -> PatientCreateScreen(child.component)
            is Child.Detail -> PatientDetailScreen(child.component)
            is Child.List -> PatientListScreen(child.component)
        }
    }
}