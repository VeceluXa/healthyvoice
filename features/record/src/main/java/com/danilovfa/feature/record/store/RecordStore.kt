package com.danilovfa.feature.record.store

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.core.base.presentation.event.PermissionStatus
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State

interface RecordStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object OnRecordClicked : Intent()

        data class OnPermissionStatusChanged(val permissionStatus: PermissionStatus) : Intent()
    }

    @Immutable
    data class State(
        val isRecording: Boolean = false,
        val recordingTimeMillis: Long = 0L,
        val amplitudes: SnapshotStateList<Int> = SnapshotStateList()
    )

    sealed class Label {
        data class Analyze(val filename: String) : Label()
        data object RequestAudioPermission : Label()
        data object ShowRationale : Label()
    }
}