package com.danilovfa.data.common.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.danilovfa.data.common.local.database.model.AnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analysis WHERE recordingId = :recordingId LIMIT 1")
    suspend fun getAnalysis(recordingId: Long): AnalysisEntity?

    @Query("SELECT * FROM analysis")
    fun observeAnalyzes(): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analysis WHERE recordingId = :recordingId LIMIT 1")
    fun observeAnalysis(recordingId: Long): Flow<AnalysisEntity?>

    @Query("DELETE FROM analysis WHERE recordingId = :recordingId")
    suspend fun deleteAnalysis(recordingId: Long)

    @Insert
    suspend fun addAnalysis(analysis: AnalysisEntity)
}