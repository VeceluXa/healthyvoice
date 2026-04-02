package com.danilovfa.presentation.analysis.store

import co.touchlab.kermit.Logger
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.domain.analysis.AnalysisRepository
import com.danilovfa.domain.common.model.Analysis
import com.danilovfa.domain.common.model.RecordingAnalysis
import com.danilovfa.domain.record.repository.RecordRepository
import com.danilovfa.domain.record.repository.model.AudioCut
import com.danilovfa.domain.record.repository.model.RecordingFull
import com.danilovfa.export.presentation.ExportWorkFactory
import com.danilovfa.export.presentation.model.ExportRequestData
import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import com.danilovfa.presentation.analysis.processing.NativeSignalProcessor
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Label
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State
import com.danilovfa.presentation.analysis.store.AnalyzeStoreFactory.Action
import com.danilovfa.presentation.analysis.store.AnalyzeStoreFactory.Msg
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt
import kotlin.time.Clock

internal class AnalyzeStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()
    private val analysisRepository: AnalysisRepository by inject()
    private val exportWorkFactory: ExportWorkFactory by inject()
    private val signalProcessor = NativeSignalProcessor()

    override fun executeAction(action: Action) = when (action) {
        Action.ProcessRecording -> getAnalysis(recordingId = state().recordingId)
        Action.ObserveAnalysis -> observeRecordingAnalysis(state().recordingId)
    }

    override fun executeIntent(intent: Intent) = when (intent) {
        Intent.OnBackClicked -> publish(Label.NavigateBack)
        Intent.RetryAnalyze -> getAnalysis(recordingId = state().recordingId)
        Intent.OnExportClicked -> export(state().recordingAnalysis)
        Intent.OnDeleteClicked -> publish(Label.ShowTodo)
    }

    private fun observeRecordingAnalysis(recordingId: Long) {
        dispatch(Msg.UpdateAnalysisLoading(true))
        analysisRepository.observeRecordingAnalysis(recordingId)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { recordingAnalysis ->
                dispatch(Msg.UpdateRecordingAnalysis(recordingAnalysis))

                if (recordingAnalysis.analysis != null) {
                    dispatch(Msg.UpdateAnalysisLoading(false))
                }
            }
            .launchIn(scope)
    }

    private fun getAnalysis(recordingId: Long) {
        scope.launch {
            dispatch(Msg.UpdateRecordingLoading(true))

            val recording = recordRepository.getFullRecording(recordingId).getOrNull()

            if (recording == null) {
                publish(Label.ShowError(text = Text.Plain("Error")))
                return@launch
            }

            getWaveform(recording.rawData)

            dispatch(Msg.UpdateRecordingLoading(false))

            if (analysisRepository.isAnalysisProcessed(recordingId).not()) {
                processRecording(recording)
                    .onSuccess {
                        saveParameters(
                            recordingId = recordingId,
                            patientId = recording.recording.patientId,
                            parameters = it
                        )
                    }
            }
        }
    }

    private suspend fun getWaveform(rawData: ShortArray) {
        val amplitudes = withContext(Dispatchers.Default) {
            rawData
                .asList()
                .chunked(10)
                .map { chunk ->
                    chunk.average().roundToInt().toShort()
                }
        }

        dispatch(Msg.UpdateAmplitudes(amplitudes))
    }

    private suspend fun processRecording(recording: RecordingFull): Result<AnalyzeParametersUi> {
        return try {
            val cutData = recording.audioData.audioCut?.let { cut ->
                val (startIndex, endIndex) = getCutIndices(cut, recording.audioData.byteRate)
                recording.rawData.copyOfRange(startIndex, endIndex + 1)
            } ?: recording.rawData

            Result.success(
                signalProcessor.analyze(
                    samples = cutData,
                    sampleRate = recording.audioData.frequency
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.withTag(TAG).e("getParameters", e)
            publish(Label.ShowError(Text.Plain(e.message ?: "")))
            Result.failure(e)
        }
    }

    private suspend fun saveParameters(
        patientId: Long,
        recordingId: Long,
        parameters: AnalyzeParametersUi
    ) {
        val analysis = Analysis(
            recordingId = recordingId,
            patientId = patientId,
            timestamp = Clock.System.now(),
            j1 = parameters.j1,
            j3 = parameters.j3,
            j5 = parameters.j5,
            s1 = parameters.s1,
            s3 = parameters.s3,
            s5 = parameters.s5,
            s11 = parameters.s11,
            f0 = parameters.f0Mean,
            f0sd = parameters.f0Sd
        )

        analysisRepository.addAnalysis(analysis)
    }

    /**
     * @return (start, end) cut indices of rawData
     */
    private fun getCutIndices(cut: AudioCut, byteRate: Long): Pair<Int, Int> {
        val startIndexByte = (cut.startMillis * byteRate / 1000).toInt()
        val endIndexByte = (cut.endMillis * byteRate / 1000).toInt()

        return Pair(startIndexByte / 2, endIndexByte / 2)
    }

    private fun export(recordingAnalysis: RecordingAnalysis?) {
        recordingAnalysis?.let { recording ->
            exportWorkFactory.create(
                ExportRequestData.Analysis(
                    patientId = recording.recording.patientId,
                    analysisId = recording.recording.id
                )
            )
        }
    }

    companion object {
        private const val TAG = "RecordingAnalyze"
    }
}
