package com.danilovfa.feature.analyze

import com.danilovfa.core.base.presentation.component.StatefulComponent
import com.danilovfa.feature.analyze.store.AnalyzeStore.Intent
import com.danilovfa.feature.analyze.store.AnalyzeStore.State

interface AnalyzeComponent : StatefulComponent<State, Intent> {
    sealed class Output {
        data object NavigateBack : Output()
    }
}