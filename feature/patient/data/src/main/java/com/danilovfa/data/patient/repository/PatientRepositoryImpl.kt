package com.danilovfa.data.patient.repository

import com.danilovfa.data.common.local.database.dao.PatientDao
import com.danilovfa.data.common.local.database.dao.PatientDetailsDao
import com.danilovfa.data.common.local.database.model.toDomain
import com.danilovfa.data.common.local.database.model.toEntity
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.PatientDetails
import com.danilovfa.domain.patient.repository.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PatientRepositoryImpl(
    private val patientDao: PatientDao,
    private val patientDetailsDao: PatientDetailsDao
) : PatientRepository {
    override fun observePatients(): Flow<List<Patient>> =
        patientDao.observePatients()
            .map { patients -> patients.map { it.toDomain() } }

    override fun observePatientDetails(id: Long): Flow<PatientDetails?> =
        patientDetailsDao.observePatientDetails(id).map { it?.toDomain() }

    override suspend fun getAllPatientDetails(): List<PatientDetails> =
        patientDetailsDao.getAllPatientDetails().map { it.toDomain() }


    override suspend fun createPatient(patient: Patient): Long =
        patientDao.addPatient(patient.toEntity())

    override suspend fun deletePatient(id: Long) =
        patientDao.deletePatient(id)

    override suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient.toEntity())
    }
}