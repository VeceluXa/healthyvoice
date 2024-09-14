package com.danilovfa.data.record.di

import com.danilovfa.data.record.data.repository.RecordRepositoryImpl
import com.danilovfa.data.record.domain.repository.RecordRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataRecordModule = module {
    single<RecordRepository> {
        RecordRepositoryImpl(
            context = androidContext()
        )
    }
}