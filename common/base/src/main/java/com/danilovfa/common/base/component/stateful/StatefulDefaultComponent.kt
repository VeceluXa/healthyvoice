package com.danilovfa.common.base.component.stateful

import com.danilovfa.common.base.component.BaseDefaultComponent
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class StatefulDefaultComponent<Intent: Any, State: Any, Label: Any>(
    componentContext: ComponentContext
) : BaseDefaultComponent(componentContext), Stateful<Intent, State> {

    protected fun observeLabels(labels: Flow<Label>, onLabel: (label: Label) -> Unit) {
        labels
            .onEach { onLabel(it) }
            .launchIn(scope)
    }
}