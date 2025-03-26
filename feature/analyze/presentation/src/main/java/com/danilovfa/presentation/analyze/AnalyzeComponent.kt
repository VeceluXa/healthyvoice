package com.danilovfa.presentation.analyze

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.presentation.analyze.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analyze.store.AnalyzeStore.State

interface AnalyzeComponent : StatefulComponent<Intent, State> {
    sealed class Output {
        data object NavigateBack : Output()
    }
}