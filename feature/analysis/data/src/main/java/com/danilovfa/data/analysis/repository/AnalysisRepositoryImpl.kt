package com.danilovfa.data.analysis.repository

import android.content.Context
import com.danilovfa.data.common.local.database.dao.AnalysisDao
import com.danilovfa.data.common.local.database.dao.RecordingAnalysisDao
import com.danilovfa.data.common.local.database.model.toDomain
import com.danilovfa.data.common.local.database.model.toEntity
import com.danilovfa.domain.analysis.AnalysisRepository
import com.danilovfa.domain.common.model.Analysis
import com.danilovfa.domain.common.model.RecordingAnalysis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AnalysisRepositoryImpl(
    private val analysisDao: AnalysisDao,
    private val recordingAnalysisDao: RecordingAnalysisDao,
    private val context: Context
) : AnalysisRepository {
    override suspend fun addAnalysis(analysis: Analysis) {
        analysisDao.addAnalysis(analysis.toEntity())
    }

    override suspend fun getAnalysis(recordingId: Long): Analysis? =
        analysisDao.getAnalysis(recordingId)?.toDomain()

    override suspend fun isAnalysisProcessed(recordingId: Long): Boolean =
        recordingAnalysisDao.getRecordingAnalysis(recordingId)?.analysis != null

    override fun observePatientRecordingAnalyzes(patientId: Long): Flow<List<RecordingAnalysis>> =
        recordingAnalysisDao.observeRecordingAnalyzes(patientId)
            .map { analyzes -> analyzes.map { it.toDomain(context) } }

    override fun observeRecordingAnalysis(recordingId: Long): Flow<RecordingAnalysis?> =
        recordingAnalysisDao.observeRecordingAnalysis(recordingId)
            .map { it?.toDomain(context) }
}