package com.danilovfa.presentation.record.main

import com.danilovfa.common.base.component.stateful.StatefulComponent
import com.danilovfa.common.base.event.Event
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.record.main.store.RecordStore.Intent
import com.danilovfa.presentation.record.main.store.RecordStore.State

internal interface RecordComponent : StatefulComponent<Intent, State> {

    sealed class Output {
        data class NavigateCut(val recordingId: Long) : Output()
        data object NavigateBack : Output()
    }

    sealed class RecordEvent : Event {
        data object OpenAppSettings : RecordEvent()
        data object OpenFilePicker : RecordEvent()
    }
}