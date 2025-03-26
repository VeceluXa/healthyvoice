package com.danilovfa.presentation.record

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.common.base.event.Event
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.record.store.RecordStore.Intent
import com.danilovfa.presentation.record.store.RecordStore.State

interface RecordComponent : StatefulComponent<Intent, State> {

    sealed class Output {
        data class Analyze(val audioData: AudioData) : Output()
    }

    sealed class RecordEvent : Event {
        data object OpenAppSettings : RecordEvent()
        data object OpenFilePicker : RecordEvent()
    }
}