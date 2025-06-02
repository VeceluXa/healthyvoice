package com.danilovfa.export.presentation

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.danilovfa.common.core.presentation.notification.NotificationChannelData
import com.danilovfa.common.core.presentation.notification.getNotificationBuilder
import com.danilovfa.common.core.presentation.notification.show
import com.danilovfa.common.resources.drawables
import com.danilovfa.common.resources.strings
import com.danilovfa.domain.export.repository.ExportRepository
import com.danilovfa.export.presentation.model.ExportRequestData
import com.danilovfa.export.presentation.utils.isAppInForeground
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


class ExportWorkFactory(
    private val context: Context
) {
    fun create(requestData: ExportRequestData) {
        val inputData = workDataOf(
            ExportWorker.INPUT_KEY to Json.encodeToString(requestData)
        )

        val request = OneTimeWorkRequestBuilder<ExportWorker>()
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}

internal class ExportWorker(
    private val exportRepository: ExportRepository,
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val json = inputData.getString(INPUT_KEY) ?: return Result.failure()

        val requestData = runCatching {
            Json.decodeFromString<ExportRequestData>(json)
        }.getOrNull() ?: return Result.failure()

        val exportFile = runCatching {
            when (requestData) {
                ExportRequestData.AllPatients -> exportRepository.exportAllPatients()
                is ExportRequestData.Patient -> exportRepository.exportPatient(requestData.patientId)
                is ExportRequestData.Analysis -> exportRepository.exportAnalysis(
                    patientId = requestData.patientId,
                    analysisId = requestData.analysisId
                ) }
        }.getOrNull() ?: return Result.failure()

        if (isAppInForeground(context)) {
            tryLaunchShareActivity(exportFile)
        } else {
            showShareNotification(exportFile)
        }

        return Result.success()
    }

    private fun tryLaunchShareActivity(zipFile: File): Boolean {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                zipFile
            )

            val intent = Intent(context, ShareActivity::class.java).apply {
                putExtra("share_uri", uri)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Examples: ActivityNotFoundException, IllegalStateException
            false
        }
    }

    private fun showShareNotification(zipFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            zipFile
        )

        val intent = Intent(context, ShareActivity::class.java).apply {
            putExtra(ShareActivity.SHARE_URI_KEY, uri)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )



        context.getNotificationBuilder(
            channel = NotificationChannelData(
                id = EXPORT_CHANNEL,
                descriptionRes = strings.export_notification_channel_description
            )
        )
            .setSmallIcon(drawables.icon_app)
            .setContentTitle(context.getString(strings.export_notification_title))
            .setContentText(context.getString(strings.export_notification_description))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
            .show(NOTIFICATION_ID, context)
    }

    companion object {
        const val INPUT_KEY = "export_params"
        const val NOTIFICATION_ID = 101
        const val EXPORT_CHANNEL = "export_channel"
    }
}