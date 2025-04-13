package com.danilovfa.presentation.root

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.danilovfa.common.uikit.composables.snackbar.SnackbarHost
import com.danilovfa.presentation.analysis.AnalyzeScreen
import com.danilovfa.presentation.patient.root.RootPatientScreen
import com.danilovfa.presentation.record.root.RecordRootScreen

@Composable
fun RootScreen(
    component: RootComponent
) {
    val childStack by component.childStack.subscribeAsState()

    SnackbarHost {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Children(stack = childStack) {
                when (val child = it.instance) {
                    is RootComponent.Child.Analyze -> AnalyzeScreen(child.component)
                    is RootComponent.Child.Patient -> RootPatientScreen(child.component)
                    is RootComponent.Child.Recording -> RecordRootScreen(child.component)
                }
            }
        }
    }
}