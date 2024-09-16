package com.danilovfa.libs.recorder.recorder.wav

import android.media.AudioFormat
import android.media.MediaRecorder
import com.danilovfa.libs.recorder.config.AudioRecordConfig
import com.danilovfa.libs.recorder.source.AudioSource

/**
 * Class for write header information
 */
object WavHeader {

    /**
     * generate wav header using [audioSource], [length] and assign them to [ByteArray]
     */
    internal fun getWavFileHeaderByteArray(audioSource: AudioSource, length: Long): ByteArray {
        val frequency = audioSource.getAudioConfig().frequency.toLong()
        val channels = if (audioSource.getAudioConfig().channel == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        val bitsPerSample = when (audioSource.getAudioConfig().audioEncoding) {
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_8BIT -> 8
            else -> 16
        }.toByte()

        return wavFileHeader(
            totalAudioLen = length - 44,
            totalDataLen = length - 44 + 36,
            longSampleRate = frequency,
            channels = channels,
            byteRate = bitsPerSample.toLong() * frequency * channels.toLong() / 8,
            bitsPerSample = bitsPerSample
        )
    }

    fun getConfigFromHeader(header: ByteArray): AudioRecordConfig {
        require(header.size >= HEADER_SIZE_BYTES) { "Header size is not valid!" }

        val bitsPerSample = header[34].toInt()

        val audioEncoding = when (bitsPerSample) {
            16 -> AudioFormat.ENCODING_PCM_16BIT
            else -> AudioFormat.ENCODING_PCM_8BIT
        }

        val channel = when (header[32].toInt() / (bitsPerSample / 8)) {
            1 -> AudioFormat.CHANNEL_IN_MONO
            else -> AudioFormat.CHANNEL_IN_STEREO
        }

        val frequency = (header[24].toInt() and 0xFF) or
                ((header[25].toInt() and 0xFF) shl 8) or
                ((header[26].toInt() and 0xFF) shl 16) or
                ((header[27].toInt() and 0xFF) shl 24)


        return AudioRecordConfig(
            audioSource = MediaRecorder.AudioSource.MIC,
            audioEncoding = audioEncoding,
            channel = channel,
            frequency = frequency,
        )
    }

    private fun wavFileHeader(
        totalAudioLen: Long, totalDataLen: Long, longSampleRate: Long,
        channels: Int, byteRate: Long, bitsPerSample: Byte
    ): ByteArray {
        val header = ByteArray(HEADER_SIZE_BYTES)
        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (channels * (bitsPerSample / 8)).toByte()
        header[33] = 0
        header[34] = bitsPerSample // bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        return header
    }

    const val HEADER_SIZE_BYTES = 44
}