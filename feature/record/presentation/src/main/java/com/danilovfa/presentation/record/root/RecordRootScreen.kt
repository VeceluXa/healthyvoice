package com.danilovfa.presentation.record.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.danilovfa.presentation.record.cut.CutScreen
import com.danilovfa.presentation.record.main.RecordScreen
import com.danilovfa.presentation.record.root.RecordRootComponent.Child

@Composable
fun RecordRootScreen(component: RecordRootComponent) {
    val childStack by component.childStack.subscribeAsState()

    Children(childStack) {
        when (val child = childStack.active.instance) {
            is Child.Cut -> CutScreen(child.component)
            is Child.Record -> RecordScreen(child.component)
        }
    }
}