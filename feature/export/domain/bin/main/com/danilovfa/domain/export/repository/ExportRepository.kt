package com.danilovfa.domain.export.repository

import java.io.File

interface ExportRepository {
    suspend fun exportAllPatients(): File?
    suspend fun exportPatient(patientId: Long): File?
    suspend fun exportAnalysis(patientId: Long, analysisId: Long): File?
}