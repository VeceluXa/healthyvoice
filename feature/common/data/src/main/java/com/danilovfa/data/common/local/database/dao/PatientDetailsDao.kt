package com.danilovfa.data.common.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.danilovfa.data.common.local.database.model.PatientAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDetailsDao {

    @Transaction
    @Query("SELECT * FROM patient")
    suspend fun getAllPatientDetails(): List<PatientAnalysisEntity>

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :id LIMIT 1")
    fun observePatientDetails(id: Long): Flow<PatientAnalysisEntity?>
}