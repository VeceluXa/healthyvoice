package com.danilovfa.presentation.record.cut.store

import co.touchlab.kermit.Logger
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.common.core.domain.log.LOG_TAG
import com.danilovfa.domain.record.repository.RecordRepository
import com.danilovfa.presentation.record.cut.store.CutStore.Intent
import com.danilovfa.presentation.record.cut.store.CutStore.Label
import com.danilovfa.presentation.record.cut.store.CutStore.State
import com.danilovfa.presentation.record.cut.store.CutStoreFactory.Action
import com.danilovfa.presentation.record.cut.store.CutStoreFactory.Msg
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.abs

internal class CutStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()

    override fun executeAction(action: Action, getState: () -> State) = when (action) {
        Action.LoadRecording -> loadRecording(getState().recordingId)
    }

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        Intent.OnAnalyzeClicked -> finishCut(
            recordingId = getState().recordingId,
            startOffset = getState().startOffset,
            endOffset = getState().endOffset
        )

        Intent.OnBackClicked -> publish(Label.ShowBackConfirmationDialog)
        Intent.OnBackConfirmed -> publish(Label.NavigateBack)
        Intent.RetryLoadRecording -> loadRecording(getState().recordingId)
        is Intent.OnEndOffsetMoved -> onEndOffsetMoved(
            startOffset = getState().startOffset,
            newEndOffset = intent.endOffset,
            duration = getState().audioData?.audioDurationMillis
        )

        is Intent.OnStartOffsetMoved -> onStartOffsetMoved(
            newStartOffset = intent.startOffset,
            endOffset = getState().endOffset
        )
    }

    private fun loadRecording(recordingId: Long) {
        scope.launch {
            recordRepository.getFullRecording(recordingId)
                .onSuccess { recording ->
                    dispatch(Msg.ChangeAudioData(recording.audioData))
                    getAmplitudes(recording.rawData)
                }
        }
    }

    private fun getAmplitudes(raw: Array<Short>) {
        val amplitudes = raw
            .toList()
            .chunked(CHUNK_SIZE)
            .map { chunk ->
                chunk.maxBy { abs(it.toInt()) }
            }
            .map {
                abs(it.toFloat() / Short.MAX_VALUE)
            }

        dispatch(Msg.UpdateAmplitudes(amplitudes))
    }

    private fun finishCut(
        recordingId: Long,
        startOffset: Int,
        endOffset: Int,
    ) {
        scope.launch {
            recordRepository.saveCut(
                recordingId = recordingId,
                startMillis = startOffset,
                endMillis = endOffset,
            )
            publish(Label.NavigateAnalyze)
        }
    }

    private fun onEndOffsetMoved(
        startOffset: Int,
        newEndOffset: Int,
        duration: Int?
    ) {
        if (newEndOffset - MIN_CUT_SIZE_MILLIS >= startOffset &&
            duration != null && newEndOffset <= duration) {
            dispatch(Msg.UpdateEndOffset(newEndOffset))
        }
    }

    private fun onStartOffsetMoved(
        newStartOffset: Int,
        endOffset: Int
    ) {
        if (newStartOffset + MIN_CUT_SIZE_MILLIS <= endOffset && newStartOffset >= 0) {
            dispatch(Msg.UpdateStartOffset(newStartOffset))
        }
    }

    companion object {
        internal const val MIN_CUT_SIZE_MILLIS = 2000
        private const val CHUNK_SIZE = 1000
    }
}