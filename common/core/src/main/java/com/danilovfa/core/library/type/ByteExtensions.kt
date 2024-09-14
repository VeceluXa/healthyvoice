package com.danilovfa.core.library.type

import kotlin.experimental.and

fun ShortArray.toByteArray(): ByteArray {
    val buffer = ByteArray(this.size * 2)

    for (i in 0..lastIndex) {
        buffer[i * 2] = (get(i) and 0xFF).toByte()
        buffer[(i * 2) + 1] = ((get(i).toInt() shr 8) and 0xFF).toByte()
    }

    return buffer
}