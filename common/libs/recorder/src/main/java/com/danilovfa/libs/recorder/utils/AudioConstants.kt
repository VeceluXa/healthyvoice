package com.danilovfa.libs.recorder.utils

import android.media.AudioFormat

object AudioConstants {
    const val FREQUENCY_44100 = 44100
    internal const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    internal const val CHANNEL = AudioFormat.CHANNEL_IN_MONO

    const val MAX_AMPLITUDE = 32762
    internal const val SILENCE_THRESHOLD = 2700
}