package com.danilovfa.presentation.cut

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.cut.store.CutStore.Intent
import com.danilovfa.presentation.cut.store.CutStore.State

interface CutComponent : StatefulComponent<Intent, State> {
    sealed class Output {
        data object NavigateBack : Output()
        data class Analyze(val data: AudioData) : Output()
    }
}