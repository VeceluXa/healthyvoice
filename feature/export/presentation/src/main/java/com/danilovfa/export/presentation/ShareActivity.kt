package com.danilovfa.export.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity

internal class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileUri = intent.getParcelableExtraCompat<Uri>(SHARE_URI_KEY) ?: run {
            finish()
            return
        }

        shareExport(fileUri)
        finish()
    }

    // Extension function to handle getParcelableExtra deprecation
    private inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(key)
        }
    }

    companion object {
        const val SHARE_URI_KEY = "share_uri"
    }
}