package com.danilovfa.presentation.record.store

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.common.base.permission.PermissionStatus
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.record.store.RecordStore.Intent
import com.danilovfa.presentation.record.store.RecordStore.Label
import com.danilovfa.presentation.record.store.RecordStore.State
import kotlinx.datetime.Instant

interface RecordStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object OnImportRecordingClicked : Intent()

        data object OnRecordStartClicked : Intent()

        data object OnRecordStopClicked : Intent()

        data class OnPermissionStatusChanged(val permissionStatus: PermissionStatus) : Intent()

        data object OnShowHelpDialogClicked : Intent()

        data class OnRecordImported(val context: Context, val uri: Uri) : Intent()
    }

    @Immutable
    data class State(
        val isRecording: Boolean = false,
        val recordingStartTime: Instant? = null,
        val amplitudes: List<Int> = emptyList()
    )

    sealed class Label {
        data class Analyze(val audioData: AudioData) : Label()
        data object RequestAudioPermission : Label()
        data object ShowRationale : Label()
        data object ShowHelpDialog : Label()
        data class ShowError(val text: Text) : Label()
        data object OpenFilePicker : Label()
    }
}