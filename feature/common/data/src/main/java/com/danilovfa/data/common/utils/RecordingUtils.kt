package com.danilovfa.data.common.utils

import android.content.Context
import java.io.File

object RecordingUtils {
    private const val RECORDING_DIR = "recordings"

    fun getRecordingsDir(context: Context): File {
        val cacheDir = context.cacheDir
        val recordingsDir = File(cacheDir, RECORDING_DIR)

        if (recordingsDir.exists().not()) {
            recordingsDir.mkdirs()
        }

        return recordingsDir
    }
}