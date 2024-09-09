package com.danilovfa.libs.recorder.di

import com.danilovfa.libs.recorder.recorder.Mp3Recorder
import com.danilovfa.libs.recorder.recorder.Recorder
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val recorderModule = module {
    factory<Recorder> {
        Mp3Recorder(
            context = androidApplication()
        )
    }
}