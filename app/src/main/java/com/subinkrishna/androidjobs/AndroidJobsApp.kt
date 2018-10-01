package com.subinkrishna.androidjobs

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

class AndroidJobsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)

        Timber.plant(Timber.DebugTree())
    }
}
