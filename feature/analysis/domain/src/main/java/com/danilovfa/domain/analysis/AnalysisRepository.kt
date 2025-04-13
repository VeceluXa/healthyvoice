package com.danilovfa.domain.analysis

import com.danilovfa.domain.common.model.Analysis
import com.danilovfa.domain.common.model.RecordingAnalysis
import kotlinx.coroutines.flow.Flow

interface AnalysisRepository {
    suspend fun addAnalysis(analysis: Analysis)
    suspend fun getAnalysis(recordingId: Long): Analysis?
    suspend fun isAnalysisProcessed(recordingId: Long): Boolean

    fun observePatientRecordingAnalyzes(patientId: Long) : Flow<List<RecordingAnalysis>>
    fun observeRecordingAnalysis(recordingId: Long) : Flow<RecordingAnalysis?>
}