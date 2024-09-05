package com.danilovfa.core.base.presentation.component

import kotlinx.coroutines.flow.StateFlow

interface Stateful<State : Any, Intent : Any> {
    val stateFlow: StateFlow<State>

    fun onIntent(intent: Intent)
}