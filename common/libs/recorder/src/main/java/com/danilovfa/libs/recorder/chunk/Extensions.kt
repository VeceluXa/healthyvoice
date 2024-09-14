package com.danilovfa.libs.recorder.chunk

import android.media.AudioRecord
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * internal extension of check [AudioChunk] has available size
 */
internal fun AudioChunk.checkChunkAvailable() = this.getReadCount() != AudioRecord.ERROR_BAD_VALUE &&
        this.getReadCount() != AudioRecord.ERROR_INVALID_OPERATION

internal fun ByteArray.getAmplitudes(): IntArray {
    val bytes = this

    val amps = IntArray(bytes.size / 2)
    var i = 0
    while (i < bytes.size) {
        var buff = bytes[i + 1].toShort()
        var buff2 = bytes[i].toShort()

        buff = (buff.toInt() and 0xFF shl 8).toShort()
        buff2 = (buff2 and 0xFF)

        val res = (buff or buff2)
        amps[if (i == 0) 0 else i / 2] = res.toInt()
        i += 2
    }
    return amps
}