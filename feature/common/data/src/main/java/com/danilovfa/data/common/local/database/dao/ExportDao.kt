package com.danilovfa.data.common.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.danilovfa.data.common.local.database.model.ExportEntity

@Dao
interface ExportDao {

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithAllAnalyses(patientId: Long): ExportEntity?

    @Transaction
    @Query("SELECT * FROM patient")
    suspend fun getAllPatientsWithAllAnalyses(): List<ExportEntity>
}
