package com.danilovfa.common.base.component.stateful

import kotlinx.coroutines.flow.StateFlow

interface Stateful<Intent : Any, State : Any> {
    val stateFlow: StateFlow<State>

    fun onIntent(intent: Intent)
}