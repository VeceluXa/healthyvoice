package com.danilovfa.export.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.danilovfa.common.resources.strings
import java.io.File

internal fun Context.shareExport(zipFile: File) {
    val uri = FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        zipFile
    )

    shareExport(uri)
}

internal fun Context.shareExport(uri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/zip"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, getString(strings.export_dialog_title))
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(chooser)
}