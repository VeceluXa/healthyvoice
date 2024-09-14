package com.danilovfa.feature.record.store

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Store
import com.danilovfa.core.base.presentation.event.PermissionStatus
import com.danilovfa.core.library.text.Text
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State
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
        data class Analyze(val filename: String) : Label()
        data object RequestAudioPermission : Label()
        data object ShowRationale : Label()
        data object ShowHelpDialog : Label()
        data class ShowError(val text: Text) : Label()
        data object OpenFilePicker : Label()
    }
}