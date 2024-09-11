package com.danilovfa.libs.recorder.utils

import android.media.AudioFormat

object RecorderConstants {
    internal const val SAMPLE_RATE = 44100
    internal const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    internal const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
    internal const val DURATION = 5

    const val MAX_AMPLITUDE = 32762
}