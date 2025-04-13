package com.danilovfa.presentation.record.cut

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.presentation.record.cut.store.CutStore.Intent
import com.danilovfa.presentation.record.cut.store.CutStore.State

internal interface CutComponent : StatefulComponent<Intent, State> {
    sealed class Output {
        data object NavigateBack : Output()
        data class Analyze(val recordingId: Long) : Output()
    }
}