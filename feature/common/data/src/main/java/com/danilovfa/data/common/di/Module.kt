package com.danilovfa.data.common.di

import androidx.room.Room
import com.danilovfa.data.common.local.database.Database
import com.danilovfa.data.common.local.database.dao.AnalysisDao
import com.danilovfa.data.common.local.database.dao.PatientDao
import com.danilovfa.data.common.local.database.dao.RecordingAnalysisDao
import com.danilovfa.data.common.local.database.dao.RecordingDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataCommonModule = module {
    single<Database> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = Database::class.java,
            name = "db_healthyvoice"
        )
            .build()
    }

    single<PatientDao> {
        get<Database>().patientDao
    }

    single<AnalysisDao> {
        get<Database>().analysisDao
    }

    single<RecordingAnalysisDao> {
        get<Database>().recordingAnalysisDao
    }

    single<RecordingDao> {
        get<Database>().recordingDao
    }
}