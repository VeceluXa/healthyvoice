package com.danilovfa.data.common.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.danilovfa.data.common.local.database.model.AnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analysis")
    suspend fun getAnalyzes(): List<AnalysisEntity>

    @Query("SELECT * FROM analysis WHERE id = :id LIMIT 1")
    suspend fun getAnalysis(id: String): AnalysisEntity?

    @Query("SELECT * FROM analysis")
    fun observeAnalyzes(): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analysis WHERE id = :id LIMIT 1")
    fun observeAnalysis(id: String): Flow<AnalysisEntity?>

    @Query("DELETE FROM analysis WHERE id = :id")
    suspend fun deleteAnalysis(id: String)

    @Insert
    suspend fun addAnalysis(analysis: AnalysisEntity)
}