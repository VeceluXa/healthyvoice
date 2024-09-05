package com.danilovfa.feature.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.danilovfa.feature.analyze.AnalyzeScreen
import com.danilovfa.feature.record.RecordScreen
import com.danilovfa.uikit.composables.snackbar.SnackbarHost

@Composable
fun RootScreen(
    component: RootComponent
) {
    val childStack by component.childStack.subscribeAsState()

    SnackbarHost {
        Children(stack = childStack) {
            when (val child = it.instance) {
                is RootComponent.Child.Analyze -> AnalyzeScreen(child.component)
                is RootComponent.Child.Record -> RecordScreen(child.component)
            }
        }
    }
}