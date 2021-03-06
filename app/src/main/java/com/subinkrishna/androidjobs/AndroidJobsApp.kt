/**
 * Copyright (C) 2018 Subinkrishna Gopi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.subinkrishna.androidjobs

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.subinkrishna.android.annotations.OpenForTesting
import timber.log.Timber

@OpenForTesting
class AndroidJobsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Leak canary
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)

        // Timber
        Timber.plant(Timber.DebugTree())
    }
}