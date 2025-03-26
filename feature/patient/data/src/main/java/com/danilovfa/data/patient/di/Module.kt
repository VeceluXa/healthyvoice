package com.danilovfa.data.patient.di

import com.danilovfa.data.common.local.database.dao.PatientDao
import com.danilovfa.data.common.local.database.dao.PatientDetailsDao
import com.danilovfa.data.patient.repository.PatientRepositoryImpl
import com.danilovfa.domain.patient.repository.PatientRepository
import org.koin.dsl.module

val dataPatientModule = module {
    single<PatientRepository> {
        PatientRepositoryImpl(
            patientDao = get<PatientDao>(),
            patientDetailsDao = get<PatientDetailsDao>()
        )
    }
}