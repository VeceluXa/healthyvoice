package com.danilovfa.healthyvoice

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.danilovfa.healthyvoice.di.modules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogging()
        initKoin()
        initPython()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            workManagerFactory()
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

    private fun initPython() {
        Python.start(AndroidPlatform(this))
    }
}