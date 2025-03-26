package com.danilovfa.presentation.cut.store

import co.touchlab.kermit.Logger
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.common.core.domain.log.LOG_TAG
import com.danilovfa.domain.record.repository.RecordRepository
import com.danilovfa.domain.record.repository.model.AudioCut
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.cut.store.CutStore.Intent
import com.danilovfa.presentation.cut.store.CutStore.Label
import com.danilovfa.presentation.cut.store.CutStore.State
import com.danilovfa.presentation.cut.store.CutStoreFactory.Action
import com.danilovfa.presentation.cut.store.CutStoreFactory.Msg
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.abs

internal class CutStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()

    override fun executeAction(action: Action, getState: () -> State) = when (action) {
        Action.LoadRecording -> loadRecording(getState().data)
    }

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        Intent.OnAnalyzeClicked -> finishCut(
            data = getState().data,
            startOffset = getState().startOffset,
            endOffset = getState().endOffset
        )

        Intent.OnBackClicked -> publish(Label.ShowBackConfirmationDialog)
        Intent.OnBackConfirmed -> publish(Label.NavigateBack)
        Intent.RetryLoadRecording -> loadRecording(getState().data)
        is Intent.OnEndOffsetMoved -> onEndOffsetMoved(
            startOffset = getState().startOffset,
            newEndOffset = intent.endOffset,
            duration = getState().data.audioDurationMillis
        )

        is Intent.OnStartOffsetMoved -> onStartOffsetMoved(
            newStartOffset = intent.startOffset,
            endOffset = getState().endOffset
        )
    }

    private fun loadRecording(data: AudioData) {
        scope.launch {
            val amplitudes = recordRepository.loadRecordingShort(data.filename)
                .getOrElse {
                    publish(Label.ShowLoadRecordingError)
                    return@launch
                }
                .toList()
                .chunked(CHUNK_SIZE)
                .map { chunk ->
                    chunk.maxBy { abs(it.toInt()) }
                }
                .map {
                    abs(it.toFloat() / Short.MAX_VALUE)
                }

            Logger.withTag(LOG_TAG).d("Amplitudes: ${amplitudes.joinToString()}")

            dispatch(Msg.UpdateAmplitudes(amplitudes))
        }
    }

    private fun finishCut(
        data: AudioData,
        startOffset: Int,
        endOffset: Int
    ) {
        val audioData = data.copy(
            audioCut = AudioCut(
                startMillis = startOffset,
                endMillis = endOffset
            )
        )

        publish(Label.NavigateAnalyze(audioData))
    }

    private fun onEndOffsetMoved(
        startOffset: Int,
        newEndOffset: Int,
        duration: Int
    ) {
        if (newEndOffset - MIN_CUT_SIZE_MILLIS >= startOffset && newEndOffset <= duration) {
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