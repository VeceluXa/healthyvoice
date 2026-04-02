package com.danilovfa.libs.recorder.recorder.wav

import android.media.AudioFormat
import com.danilovfa.libs.recorder.config.AudioRecordConfig
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import kotlin.math.min

data class DecodedWavPcm(
    val samples: ShortArray,
    val config: AudioRecordConfig,
    val channels: Int,
    val bitsPerSample: Int,
    val audioLengthBytes: Int,
    val durationMillis: Int,
)

object WavPcmReader {
    fun read(file: File): DecodedWavPcm =
        FileInputStream(file).use(::read)

    fun read(inputStream: InputStream): DecodedWavPcm {
        val header = ByteArray(WavHeader.HEADER_SIZE_BYTES)
        val headerBytesRead = inputStream.read(header)
        require(headerBytesRead == WavHeader.HEADER_SIZE_BYTES) { "WAV header is incomplete" }

        val config = WavHeader.getConfigFromHeader(header)
        val audioLengthBytes = WavHeader.getAudioLengthBytes(header)
        val bitsPerSample = when (config.audioEncoding) {
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_8BIT -> 8
            else -> error("Unsupported WAV encoding: ${config.audioEncoding}")
        }
        val channels = when (config.channel) {
            AudioFormat.CHANNEL_IN_MONO -> 1
            else -> 2
        }

        val remainingBytes = inputStream.readBytes()
        val pcmBytes = remainingBytes.copyOf(min(audioLengthBytes, remainingBytes.size))
        val byteRate = (bitsPerSample.toLong() / 8L) * config.frequency * channels.toLong()
        val durationMillis = if (byteRate == 0L) 0 else ((pcmBytes.size.toFloat() / byteRate) * 1000f).toInt()

        return DecodedWavPcm(
            samples = decodeSamples(pcmBytes, bitsPerSample),
            config = config,
            channels = channels,
            bitsPerSample = bitsPerSample,
            audioLengthBytes = pcmBytes.size,
            durationMillis = durationMillis,
        )
    }

    private fun decodeSamples(bytes: ByteArray, bitsPerSample: Int): ShortArray = when (bitsPerSample) {
        16 -> {
            val sampleCount = bytes.size / 2
            ShortArray(sampleCount).also { samples ->
                var byteIndex = 0
                for (sampleIndex in 0 until sampleCount) {
                    val low = bytes[byteIndex].toInt() and 0xFF
                    val high = bytes[byteIndex + 1].toInt()
                    samples[sampleIndex] = ((high shl 8) or low).toShort()
                    byteIndex += 2
                }
            }
        }

        8 -> ShortArray(bytes.size) { index ->
            (((bytes[index].toInt() and 0xFF) - 128) shl 8).toShort()
        }

        else -> error("Unsupported PCM depth: $bitsPerSample")
    }
}
