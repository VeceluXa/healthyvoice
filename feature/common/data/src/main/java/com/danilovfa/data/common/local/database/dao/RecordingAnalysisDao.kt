package com.danilovfa.data.common.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.danilovfa.data.common.local.database.model.RecordingAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingAnalysisDao {
    @Query("SELECT * FROM recording WHERE patientId = :patientId")
    @Transaction
    fun observeRecordingAnalyzes(patientId: Long) : Flow<List<RecordingAnalysisEntity>>

    @Query("SELECT * FROM recording WHERE id = :recordingId LIMIT 1")
    fun observeRecordingAnalysis(recordingId: Long) : Flow<RecordingAnalysisEntity?>

    @Query("SELECT * FROM recording WHERE id = :recordingId LIMIT 1")
    suspend fun getRecordingAnalysis(recordingId: Long) : RecordingAnalysisEntity?
}