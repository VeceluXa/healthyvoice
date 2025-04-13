package com.danilovfa.presentation.analysis.store

import co.touchlab.kermit.Logger
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.domain.analysis.AnalysisRepository
import com.danilovfa.domain.common.model.Analysis
import com.danilovfa.domain.record.repository.RecordRepository
import com.danilovfa.domain.record.repository.model.AudioCut
import com.danilovfa.domain.record.repository.model.RecordingFull
import com.danilovfa.presentation.analysis.model.AnalyzeParametersUi
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Intent
import com.danilovfa.presentation.analysis.store.AnalyzeStore.Label
import com.danilovfa.presentation.analysis.store.AnalyzeStore.State
import com.danilovfa.presentation.analysis.store.AnalyzeStoreFactory.Action
import com.danilovfa.presentation.analysis.store.AnalyzeStoreFactory.Msg
import com.danilovfa.libs.recorder.utils.AudioConstants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt

internal class AnalyzeStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()
    private val analysisRepository: AnalysisRepository by inject()

    override fun executeAction(action: Action, getState: () -> State) = when (action) {
        Action.ProcessRecording -> getAnalysis(recordingId = getState().recordingId)
        Action.ObserveAnalysis -> observeRecordingAnalysis(getState().recordingId)
    }

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        Intent.OnBackClicked -> publish(Label.NavigateBack)
        Intent.RetryAnalyze -> getAnalysis(recordingId = getState().recordingId)
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

    private suspend fun getWaveform(rawData: Array<Short>) {
        val amplitudes = withContext(Dispatchers.Default) {
            rawData
                .toList()
                .chunked(10)
                .map { chunk ->
                    chunk.average().roundToInt().toShort()
                }
        }

        dispatch(Msg.UpdateAmplitudes(amplitudes))
    }

    private suspend fun processRecording(recording: RecordingFull): Result<AnalyzeParametersUi> {
        if (Python.isStarted().not()) {
            Logger.withTag(TAG).e("Python is not initialized!")
            publish(Label.ShowError())
            return Result.failure(Exception("Python is not initialized"))
        }

        val analyzer = Python.getInstance().getModule("signal_processor")

        return try {
            val cutData = recording.audioData.audioCut?.let { cut ->
                val (startIndex, endIndex) = getCutIndices(cut, recording.audioData.byteRate)
                recording.rawData.copyOfRange(startIndex, endIndex + 1)
            } ?: recording.rawData

            Result.success(analyze(analyzer, cutData))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.withTag(TAG).e("getParameters", e)
            publish(Label.ShowError(Text.Plain(e.message ?: "")))
            Result.failure(e)
        }
    }

    private suspend fun analyze(analyzer: PyObject, recordingRaw: Array<Short>): AnalyzeParametersUi {
        var startTime = Clock.System.now().toEpochMilliseconds()

        Logger.withTag(TAG).d("Started python analysis")

        val pySegments = withContext(Dispatchers.Default) {
            analyzer.callAttr("voice_segmentation", recordingRaw, AudioConstants.FREQUENCY_44100)
        }.asList()

        val pyWmMethod = withContext(Dispatchers.Default) {
            analyzer.callAttr("WM_method", recordingRaw, pySegments[1], AudioConstants.FREQUENCY_44100, pySegments[0])
        }.asList()

        Logger.withTag(TAG)
            .d("voice_segmentation: ${Clock.System.now().toEpochMilliseconds() - startTime} ms")
        startTime = Clock.System.now().toEpochMilliseconds()

        val pyParams = withContext(Dispatchers.Default) {
            analyzer.callAttr("voice_parameters", recordingRaw, pyWmMethod[1], pyWmMethod[0])
        }

        Logger.withTag(TAG)
            .d("voice_parameters: ${Clock.System.now().toEpochMilliseconds() - startTime} ms")

        return getParametersFromProcessing(pyParams)
    }

    private fun getParametersFromProcessing(pyParams: PyObject): AnalyzeParametersUi {
        val paramsList = pyParams.asList().map {
            it.toFloat()
        }

        return AnalyzeParametersUi(
            j1 = paramsList.getOrNull(0) ?: 0f,
            j3 = paramsList.getOrNull(1) ?: 0f,
            j5 = paramsList.getOrNull(2) ?: 0f,
            s1 = paramsList.getOrNull(3) ?: 0f,
            s3 = paramsList.getOrNull(4) ?: 0f,
            s5 = paramsList.getOrNull(5) ?: 0f,
            s11 = paramsList.getOrNull(6) ?: 0f,
            f0Mean = paramsList.getOrNull(7) ?: 0f,
            f0Sd = paramsList.getOrNull(8) ?: 0f
        )
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

    companion object {
        private const val TAG = "RecordingAnalyze"
    }
}