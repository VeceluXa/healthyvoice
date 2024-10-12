package com.danilovfa.feature.analyze.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.danilovfa.core.library.log.LOG_TAG
import com.danilovfa.core.library.text.Text
import com.danilovfa.data.common.model.AudioCut
import com.danilovfa.data.common.model.AudioData
import com.danilovfa.data.record.domain.repository.RecordRepository
import com.danilovfa.feature.analyze.model.AnalyzeParametersUi
import com.danilovfa.feature.analyze.store.AnalyzeStore.Intent
import com.danilovfa.feature.analyze.store.AnalyzeStore.Label
import com.danilovfa.feature.analyze.store.AnalyzeStore.State
import com.danilovfa.feature.analyze.store.AnalyzeStoreFactory.Action
import com.danilovfa.feature.analyze.store.AnalyzeStoreFactory.Msg
import com.danilovfa.libs.recorder.utils.AudioConstants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import kotlin.math.roundToInt

internal class AnalyzeStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()

    override fun executeAction(action: Action, getState: () -> State) = when (action) {
        Action.ProcessRecording -> processRecording(getState().audioData)
    }

    override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
        Intent.OnBackClicked -> publish(Label.ShowConfirmNavigateBack)
        Intent.OnBackConfirmed -> publish(Label.NavigateBack)
        Intent.RetryAnalyze -> processRecording(getState().audioData)
    }

    private fun processRecording(audioData: AudioData) {
        scope.launch {
            dispatch(Msg.UpdateLoading(true))
            val recordingRaw = recordRepository.loadRecordingShort(audioData.filename).getOrElse {
                Timber.tag(TAG).e(it)
                publish(Label.ShowError(text = Text.Plain(it.message ?: "")))
                dispatch(Msg.UpdateLoading(false))
                return@launch
            }
            dispatch(Msg.UpdateLoading(false))

            scope.launch { getParameters(audioData, recordingRaw) }
            scope.launch { getWaveform(audioData, recordingRaw) }
        }
    }

    private suspend fun getWaveform(audioData: AudioData, rawData: Array<Short>) {
        val amplitudes = rawData
            .toList()
            .chunked(10)
            .map { chunk ->
                chunk.average().roundToInt().toShort()
            }

        dispatch(Msg.UpdateAmplitudes(amplitudes))
    }

    private suspend fun getParameters(audioData: AudioData, rawData: Array<Short>) {
        if (Python.isStarted().not()) {
            Timber.tag(TAG).e("Python is not initialized!")
            publish(Label.ShowError())
            return
        }

        val analyzer = Python.getInstance().getModule("signal_processor")

        try {
            val cutData = audioData.audioCut?.let { cut ->
                val (startIndex, endIndex) = getCutIndices(cut, audioData.byteRate)
                rawData.copyOfRange(startIndex, endIndex + 1)
            } ?: rawData

            analyze(analyzer, cutData)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e)
            publish(Label.ShowError())
        }
    }

    private suspend fun analyze(analyzer: PyObject, recordingRaw: Array<Short>) {
        var startTime = Clock.System.now().toEpochMilliseconds()

        Timber.tag(TAG).d("Started python analysis")

        val pySegments = withContext(Dispatchers.Default) {
            analyzer.callAttr("voice_segmentation", recordingRaw, AudioConstants.FREQUENCY_44100)
        }.asList()

        val pyWmMethod = withContext(Dispatchers.Default) {
            analyzer.callAttr("WM_method", recordingRaw, pySegments[1], AudioConstants.FREQUENCY_44100, pySegments[0])
        }.asList()

        Timber.tag(TAG)
            .d("voice_segmentation: ${Clock.System.now().toEpochMilliseconds() - startTime} ms")
        startTime = Clock.System.now().toEpochMilliseconds()

        val pyParams = withContext(Dispatchers.Default) {
            analyzer.callAttr("voice_parameters", recordingRaw, pyWmMethod[1], pyWmMethod[0])
        }

        Timber.tag(TAG)
            .d("voice_parameters: ${Clock.System.now().toEpochMilliseconds() - startTime} ms")

        updateParameters(pyParams)

    }

    private fun updateParameters(pyParams: PyObject) {
        val paramsList = pyParams.asList().map {
            it.toFloat()
        }

        val params = AnalyzeParametersUi(
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

        Timber.tag(TAG).d("Params: $params")

        dispatch(Msg.UpdateParameters(params))

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