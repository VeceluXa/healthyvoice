package com.danilovfa.libs.recorder.chunk

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * [AudioChunk] which handle [ByteArray]
 */
class ByteArrayAudioChunk(var bytes: ByteArray) : AudioChunk {
    private var _readCount: Int = 0

    /**
     * see [AudioChunk.getMaxAmplitude]
     */
    override fun getMaxAmplitude(bufferSize: Int): Int {
        val amplitudes = bytes.getAmplitudes()
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
    override fun toByteArray(): ByteArray = bytes

    /**
     * see [AudioChunk.toShortArray]
     */
    override fun toShortArray(): ShortArray {
        val shorts = ShortArray(bytes.size / 2)
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shorts)
        return shorts
    }

    /**
     * see [AudioChunk.getReadCount]
     */
    override fun getReadCount(): Int = _readCount

    /**
     * see [AudioChunk.setReadCount]
     */
    override fun setReadCount(readCount: Int) = this.apply { _readCount = readCount }
}