package com.danilovfa.data.export.di

import com.danilovfa.data.common.local.database.dao.ExportDao
import com.danilovfa.data.export.repository.ExportRepositoryImpl
import com.danilovfa.domain.export.repository.ExportRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataExportModule = module {
    single<ExportRepository> {
        ExportRepositoryImpl(
            context = androidContext(),
            exportDao = get<ExportDao>()
        )
    }
}