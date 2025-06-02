package com.danilovfa.export.presentation.di

import androidx.work.WorkManager
import com.danilovfa.domain.export.repository.ExportRepository
import com.danilovfa.export.presentation.ExportWorkFactory
import com.danilovfa.export.presentation.ExportWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val presentationExportModule = module {
    single<ExportWorkFactory> {
        ExportWorkFactory(
            context = androidContext()
        )
    }

    worker {
        ExportWorker(
            exportRepository = get<ExportRepository>(),
            context = get(),
            workerParams = get()
        )
    }
}