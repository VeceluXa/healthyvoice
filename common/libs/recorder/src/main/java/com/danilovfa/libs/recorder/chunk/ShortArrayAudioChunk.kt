package com.danilovfa.libs.recorder.chunk

import com.danilovfa.libs.recorder.utils.AudioConstants
import kotlin.experimental.and
import kotlin.math.abs
import kotlin.math.log10

class ShortArrayAudioChunk(var shorts: ShortArray) : AudioChunk {
    private var _readCount: Int = 0

    /**
     * see [AudioChunk.getMaxAmplitude]
     */
    override fun getMaxAmplitude(bufferSize: Int): Int {
        val amplitudes = toByteArray().getAmplitudes()
        var major = 0
        var minor = 0
        for (i in amplitudes) {
            if (i > major) major = i
            if (i < minor) minor = i
        }
        return major.coerceAtLeast(minor * -1)
    }

    /**
     * see [AudioChunk.toByteArray]
     */
    override fun toByteArray(): ByteArray {
        val buffer = ByteArray(getReadCount() * 2)

        var byteIndex = 0
        var shortIndex = 0

        while (shortIndex != getReadCount()) {
            val short = shorts[shortIndex]
            val left = short and 0x00FF
            val right = (short and 0xFF00.toShort()).toInt() shr 8

            buffer[byteIndex] = left.toByte()
            buffer[byteIndex + 1] = right.toByte()
            ++shortIndex
            byteIndex += 2
        }

        return buffer
    }

    /**
     * see [AudioChunk.toShortArray]
     */
    override fun toShortArray(): ShortArray = shorts

    /**
     * see [AudioChunk.getReadCount]
     */
    override fun getReadCount(): Int = _readCount

    /**
     * see [AudioChunk.setReadCount]
     */
    override fun setReadCount(readCount: Int) = this.apply { _readCount = readCount }

    /**
     * find first peak value of [shorts]
     */
    fun findFirstIndex(): Int =
        shorts.indexOfFirst { it >= AudioConstants.SILENCE_THRESHOLD || it <= -AudioConstants.SILENCE_THRESHOLD }

}