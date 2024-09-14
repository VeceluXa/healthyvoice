package com.danilovfa.healthyvoice

import android.app.Application
import com.danilovfa.healthyvoice.di.modules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogging()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(modules)
            if (BuildConfig.DEBUG) {
                androidLogger(Level.DEBUG)
            }
        }
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}