package com.danilovfa.common.core.presentation.extensions

import android.content.Context
import android.net.Uri
import java.io.File

fun Uri.getWavFile(context: Context): File? {
    // Get MIME type from ContentResolver
    val mimeType = context.contentResolver.getType(this)
    if (mimeType != "audio/wav" && mimeType != "audio/x-wav") {
        // Not a WAV file
        return null
    }

    // Copy content to a temporary file
    val fileName = "temp_audio.wav"
    val tempFile = File(context.cacheDir, fileName)

    context.contentResolver.openInputStream(this)?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return tempFile
}