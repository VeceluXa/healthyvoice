package com.danilovfa.presentation.analysis

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State

interface AnalyzeComponent : StatefulComponent<Intent, State> {
    sealed class Output {
        data object NavigateBack : Output()
    }
}