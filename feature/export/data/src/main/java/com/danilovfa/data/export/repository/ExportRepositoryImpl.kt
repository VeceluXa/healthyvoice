package com.danilovfa.data.export.repository

import android.content.Context
import com.danilovfa.data.common.local.database.dao.ExportDao
import com.danilovfa.data.common.local.database.model.ExportEntity
import com.danilovfa.data.common.utils.RecordingUtils
import com.danilovfa.domain.export.repository.ExportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.coroutines.CoroutineContext

class ExportRepositoryImpl(
    private val context: Context,
    private val exportDao: ExportDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) : ExportRepository {

    override suspend fun exportAllPatients(): File? = withContext(ioDispatcher) {
        val exportDataEntity = exportDao
            .getAllPatientsWithAllAnalyses()
            .takeUnless { it.isEmpty() }
            ?: return@withContext null

        return@withContext exportData(exportDataEntity)
    }

    override suspend fun exportPatient(patientId: Long): File? = withContext(ioDispatcher) {
        val exportDataEntity =
            exportDao.getPatientWithAllAnalyses(patientId) ?: return@withContext null
        return@withContext exportData(listOf(exportDataEntity))
    }

    override suspend fun exportAnalysis(patientId: Long, analysisId: Long): File? =
        withContext(ioDispatcher) {
            val exportDataEntity =
                exportDao.getPatientWithAllAnalyses(patientId)?.let { exportEntity ->
                    exportEntity.copy(analysis = exportEntity.analysis.filter { it.recording.id == analysisId })
                } ?: return@withContext null

            return@withContext exportData(listOf(exportDataEntity))
        }

    private fun exportData(data: List<ExportEntity>): File {
        val recordingDir = RecordingUtils.getRecordingsDir(context)
        val exportDir = File(context.cacheDir, "export")
        exportDir.mkdirs()

        val csvFile = File(exportDir, "${UUID.randomUUID()}.$CSV_FORMAT")
        exportToCsv(data, csvFile)

        val recordingFiles = data.map { exportEntity ->
            exportEntity.analysis.mapNotNull { recording ->
                File(recordingDir, recording.recording.filename)
                    .takeIf { it.exists() && it.isFile }
                    ?.let {
                        RecordingData(
                            patientId = recording.recording.patientId,
                            recordingId = recording.recording.id,
                            file = it
                        )
                    }
            }
        }.flatten()

        val zipFile = File(exportDir, "export-${Clock.System.now()}.$ZIP_FORMAT")
        createZipArchive(zipFile, csvFile, recordingFiles)
        return zipFile
    }

    private fun exportToCsv(patientsWithAnalyses: List<ExportEntity>, outputFile: File) {
        outputFile.printWriter().use { writer ->
            writer.write("\uFEFF") // UTF-8 BOM
            writer.println(buildString {
                appendSeparated("patient_id")
                appendSeparated("patient_name")
                appendSeparated("patient_sex")
                appendSeparated("patient_birth_date")
                appendSeparated("recording_id")
                appendSeparated("recording_start_millis")
                appendSeparated("recording_end_millis")
                appendSeparated("analysis_j1")
                appendSeparated("analysis_j3")
                appendSeparated("analysis_j5")
                appendSeparated("analysis_s1")
                appendSeparated("analysis_s3")
                appendSeparated("analysis_s5")
                appendSeparated("analysis_s11")
                appendSeparated("analysis_f0")
                append("analysis_f0_sd")
            })
            for (entry in patientsWithAnalyses) {
                val patient = entry.patient
                for (analysisEntity in entry.analysis) {
                    analysisEntity.analysis?.let { analysis ->
                        writer.println(buildString {
                            appendSeparated(patient.id.toString())
                            appendSeparated(patient.name)
                            appendSeparated(patient.sex.toString())
                            appendSeparated(patient.birthDate.toString())
                            appendSeparated(analysis.recordingId.toString())
                            appendSeparated(analysisEntity.recording.cutStartMillis.toString())
                            appendSeparated(analysisEntity.recording.cutEndMillis.toString())
                            appendSeparated(analysis.j1.toString())
                            appendSeparated(analysis.j3.toString())
                            appendSeparated(analysis.j5.toString())
                            appendSeparated(analysis.s1.toString())
                            appendSeparated(analysis.s3.toString())
                            appendSeparated(analysis.s5.toString())
                            appendSeparated(analysis.s11.toString())
                            appendSeparated(analysis.f0.toString())
                            append(analysis.f0sd.toString())
                        })
                    }
                }
            }
        }
    }

    private fun createZipArchive(zipFile: File, csvFile: File, recordingsFiles: List<RecordingData>) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { out ->
            FileInputStream(csvFile).use { inputStream ->
                val entry = ZipEntry("patients.$CSV_FORMAT")
                out.putNextEntry(entry)
                inputStream.copyTo(out)
                out.closeEntry()
            }
            recordingsFiles.forEach { recording ->
                FileInputStream(recording.file).use { inputStream ->
                    val entry = ZipEntry("recordings/${recording.patientId}-${recording.recordingId}.$WAV_FORMAT")
                    out.putNextEntry(entry)
                    inputStream.copyTo(out)
                    out.closeEntry()
                }
            }
        }
    }

    private data class RecordingData(
        val patientId: Long,
        val recordingId: Long,
        val file: File
    )

    private fun StringBuilder.appendSeparated(string: String) = append("$string,")

    companion object {
        private const val CSV_FORMAT = "CSV"
        private const val ZIP_FORMAT = "ZIP"
        private const val WAV_FORMAT = "WAV"
    }
}