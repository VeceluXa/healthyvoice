package com.danilovfa.domain.patient.repository

import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.PatientDetails
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    fun observePatients(): Flow<List<Patient>>
    fun observePatientDetails(id: Long): Flow<PatientDetails?>
    suspend fun getAllPatientDetails(): List<PatientDetails>
    suspend fun createPatient(patient: Patient): Long
    suspend fun deletePatient(id: Long)
    suspend fun updatePatient(patient: Patient)
}