package com.danilovfa.data.analysis.di

import com.danilovfa.data.analysis.repository.AnalysisRepositoryImpl
import com.danilovfa.data.common.local.database.dao.AnalysisDao
import com.danilovfa.data.common.local.database.dao.RecordingAnalysisDao
import com.danilovfa.domain.analysis.AnalysisRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataAnalysisModule = module {
    single<AnalysisRepository> {
        AnalysisRepositoryImpl(
            analysisDao = get<AnalysisDao>(),
            recordingAnalysisDao = get<RecordingAnalysisDao>(),
            context = androidContext()
        )
    }
}