package com.danilovfa.domain.patient.repository

import com.danilovfa.domain.common.model.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    fun observePatients(): Flow<List<Patient>>
    fun observePatient(id: Long): Flow<Patient?>
    suspend fun createPatient(patient: Patient): Long
    suspend fun deletePatient(id: Long)
    suspend fun updatePatient(patient: Patient)
    suspend fun updateNote(patientId: Long, note: String)
}