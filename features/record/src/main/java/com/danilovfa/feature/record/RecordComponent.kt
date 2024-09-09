package com.danilovfa.feature.record

import com.danilovfa.core.base.presentation.component.StatefulComponent
import com.danilovfa.core.base.presentation.event.Event
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.State

interface RecordComponent : StatefulComponent<State, Intent> {

    sealed class Output {
        data class Analyze(val filename: String) : Output()
    }

    sealed class Events : Event {
        data object OpenAppSettings : Events()
    }
}