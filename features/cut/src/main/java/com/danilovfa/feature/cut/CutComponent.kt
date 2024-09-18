package com.danilovfa.feature.cut

import com.danilovfa.core.base.presentation.component.StatefulComponent
import com.danilovfa.data.common.model.AudioData
import com.danilovfa.feature.cut.store.CutStore.Intent
import com.danilovfa.feature.cut.store.CutStore.State

interface CutComponent : StatefulComponent<State, Intent> {
    sealed class Output {
        data object NavigateBack : Output()
        data class Analyze(val data: AudioData) : Output()
    }
}