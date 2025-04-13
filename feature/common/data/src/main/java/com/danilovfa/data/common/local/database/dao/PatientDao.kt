package com.danilovfa.data.common.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.danilovfa.data.common.local.database.model.PatientEntity
import com.danilovfa.domain.common.model.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patient")
    suspend fun getPatients(): List<PatientEntity>

    @Query("SELECT * FROM patient WHERE id = :id LIMIT 1")
    suspend fun getPatient(id: Long): PatientEntity?

    @Query("SELECT * FROM patient")
    fun observePatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patient WHERE id = :id LIMIT 1")
    fun observePatient(id: Long): Flow<PatientEntity?>

    @Query("DELETE FROM patient WHERE id = :id")
    suspend fun deletePatient(id: Long)

    @Insert
    suspend fun addPatient(patient: PatientEntity): Long

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Query("UPDATE patient SET note = :note WHERE id = :patientId")
    suspend fun updateNote(patientId: Long, note: String)
}