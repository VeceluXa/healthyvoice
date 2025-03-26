package com.danilovfa.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.danilovfa.presentation.cut.CutScreen
import com.danilovfa.presentation.record.RecordScreen
import com.danilovfa.common.uikit.composables.snackbar.SnackbarHost
import com.danilovfa.presentation.analyze.AnalyzeScreen

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
                is RootComponent.Child.Cut -> CutScreen(child.component)
            }
        }
    }
}