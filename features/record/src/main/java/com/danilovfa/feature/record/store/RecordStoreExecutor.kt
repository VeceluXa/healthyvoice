package com.danilovfa.feature.record.store

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.net.Uri
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.danilovfa.core.base.presentation.event.PermissionStatus
import com.danilovfa.core.library.text.Text
import com.danilovfa.data.common.model.AudioData
import com.danilovfa.data.record.domain.repository.RecordRepository
import com.danilovfa.feature.record.store.RecordStore.Intent
import com.danilovfa.feature.record.store.RecordStore.Label
import com.danilovfa.feature.record.store.RecordStore.State
import com.danilovfa.feature.record.store.RecordStoreFactory.Msg
import com.danilovfa.libs.recorder.config.AudioRecordConfig
import com.danilovfa.libs.recorder.recorder.AudioRecorder
import com.danilovfa.libs.recorder.recorder.WavAudioRecorder
import com.danilovfa.libs.recorder.recorder.wav.WavHeader
import com.danilovfa.libs.recorder.source.DefaultAudioSource
import com.danilovfa.libs.recorder.writer.DefaultRecordWriter
import com.danilovfa.resources.drawable.strings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.util.UUID

internal class RecordStoreExecutor : KoinComponent,
    CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {

    private val recordRepository: RecordRepository by inject()
    private var recorder: AudioRecorder? = null

    private var recorderJob: Job? = null

    /**
     * Needed because compose calls permission change on screen creation
     */
    private var hasRecordClicked: Boolean = false

    override fun executeIntent(intent: Intent, getState: () -> State): Unit = when (intent) {
        Intent.OnRecordStartClicked -> onRecordClicked()
        is Intent.OnPermissionStatusChanged -> onPermissionStatusChanged(
            permissionStatus = intent.permissionStatus,
        )

        Intent.OnRecordStopClicked -> stopRecording()
        Intent.OnShowHelpDialogClicked -> publish(Label.ShowHelpDialog)
        Intent.OnImportRecordingClicked -> publish(Label.OpenFilePicker)
        is Intent.OnRecordImported -> importRecording(intent.uri, intent.context)
    }

    private fun onRecordClicked() {
        hasRecordClicked = true
        publish(Label.RequestAudioPermission)
    }

    private fun onPermissionStatusChanged(permissionStatus: PermissionStatus) =
        when (permissionStatus) {
            PermissionStatus.Denied -> publish(Label.ShowRationale)

            PermissionStatus.Granted -> {
                if (hasRecordClicked) {
                    dispatch(Msg.UpdatePlaying(true))
                    startRecording()
                }
                Unit
            }

            PermissionStatus.NeedsRationale -> publish(Label.ShowRationale)
        }

    private fun startRecording() {
        hasRecordClicked = false

        val filename = "${UUID.randomUUID()}.wav"
        val file = File(recordRepository.getRecordingsDir(), filename)

        val config = AudioRecordConfig.defaultConfig()
        val audioSource = DefaultAudioSource(audioRecordConfig = config)

        recorder = WavAudioRecorder(
            file = file,
            recordWriter = DefaultRecordWriter(
                audioSource = audioSource,
                amplitudeListener = {
                    Timber.tag(TAG).d("Amplitude: $it")
                    dispatch(Msg.AddAmplitude(it))
                }
            )
        )


        recorderJob?.cancel()
        scope.launch {
            recorderJob = launch {
                recorder?.startRecording()
            }

            startTimer()
            stopRecording()
            publish(Label.Analyze(getAudioDataFromFile(file)))
        }
    }

    private suspend fun startTimer() {
        val startTime = Clock.System.now()
        dispatch(Msg.UpdateRecordingStartTime(startTime))

        var currentTime = Clock.System.now()

        while (currentTime.toEpochMilliseconds() - startTime.toEpochMilliseconds() <= END_TIME_MILLIS) {
            delay(TIMER_DELAY)
            currentTime = Clock.System.now()
        }
    }

    private fun stopRecording() {
        dispatch(Msg.UpdatePlaying(false))
        dispatch(Msg.UpdateRecordingStartTime(null))
        dispatch(Msg.UpdateAmplitudes(emptyList()))
        recorder?.stopRecording()
        recorderJob?.cancel()
    }

    private fun importRecording(uri: Uri, context: Context) {
        scope.launch {
            val fileType = uri.path?.let { path ->
                path.substring(path.lastIndexOf('.'))
            }

            if (fileType != ".wav") {
                publish(Label.ShowError(Text.Resource(strings.record_wrong_format)))
                return@launch
            }

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes().takeUnless { it.size < WavHeader.HEADER_SIZE_BYTES }
                    ?.let { bytes ->
                        recordRepository.saveEncodedRecording(bytes)
                            .onFailure {
                                publish(Label.ShowError(Text.Plain(it.message ?: "")))
                            }
                            .onSuccess { filename ->
                                publish(
                                    Label.Analyze(
                                        getAudioDataFromHeader(
                                            header = bytes.copyOfRange(0, WavHeader.HEADER_SIZE_BYTES + 1),
                                            filename = filename
                                        )
                                    )
                                )
                            }
                    }
            }
        }
    }

    private suspend fun getAudioDataFromFile(
        file: File
    ): AudioData {
        val header = ByteArray(WavHeader.HEADER_SIZE_BYTES)

        withContext(Dispatchers.IO) {
            FileInputStream(file).use {
                it.read(header)
            }
        }

        return getAudioDataFromHeader(
            header = header,
            filename = file.name
        )
    }

    private fun getAudioDataFromHeader(
        header: ByteArray,
        filename: String,
    ): AudioData {
        val config = WavHeader.getConfigFromHeader(header)
        val audioLength = WavHeader.getAudioLengthBytes(header)

        val bufferSize = AudioRecord.getMinBufferSize(
            config.frequency,
            config.channel,
            config.audioEncoding
        )

        val channels = if (config.channel == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        val bitsPerSample = when (config.audioEncoding) {
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_8BIT -> 8
            else -> 16
        }.toByte()

        val byteRate = (bitsPerSample.toLong() / 8) * config.frequency * channels.toLong()
        val durationMillis = ((audioLength.toFloat() / byteRate) * 1000).toInt()

        return getAudioData(
            filename = filename,
            config = config,
            bufferSize = bufferSize,
            durationMillis = durationMillis
        )
    }

    private fun getAudioData(
        filename: String,
        config: AudioRecordConfig,
        bufferSize: Int,
        durationMillis: Int
    ): AudioData = AudioData(
        filename = filename,
        frequency = config.frequency,
        channels = when (config.channel) {
            AudioFormat.CHANNEL_IN_MONO -> 1
            else -> 2
        },
        bitsPerSample = when (config.audioEncoding) {
            AudioFormat.ENCODING_PCM_16BIT -> 16
            else -> 8
        },
        bufferSize = bufferSize,
        audioDurationMillis = durationMillis
    )

    companion object {
        private const val TAG = "RecordStore"
        private const val TIMER_DELAY = 50L
        private const val END_TIME_MILLIS = 6_000L
    }
}