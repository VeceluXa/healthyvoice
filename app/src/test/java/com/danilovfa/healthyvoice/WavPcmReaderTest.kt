package com.danilovfa.healthyvoice

import com.danilovfa.libs.recorder.recorder.wav.WavPcmReader
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WavPcmReaderTest {
    @Test
    fun benchmarkAssetHasExpectedHeader() {
        val assetFile = File("src/main/assets/benchmarks/117_a.wav")
        assertTrue(assetFile.exists())

        val decoded = WavPcmReader.read(assetFile)

        assertEquals(44_100, decoded.config.frequency)
        assertEquals(1, decoded.channels)
        assertEquals(16, decoded.bitsPerSample)
        assertEquals(132_300, decoded.samples.size)
        assertTrue(decoded.durationMillis > 0)
    }
}
