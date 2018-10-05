package com.subinkrishna.androidjobs.ui.listing

import android.app.Application
import com.subinkrishna.androidjobs.service.AndroidJobsApi
import com.subinkrishna.androidjobs.service.converter.JobListingParser
import com.subinkrishna.androidjobs.service.model.JobListing
import io.reactivex.Single
import timber.log.Timber

/**
 * NOTE: For testing only. Remove after testing
 */
class TestJobsApi(val app: Application) : AndroidJobsApi {
    override fun getJobs(): Single<List<JobListing>> {
        Timber.i("API: Fetch jobs!")
        return Single.fromCallable {
            val fileContents = app.assets
                    .open("sample.html")
                    .bufferedReader()
                    .use { it.readText() }
            JobListingParser.parse(fileContents)
        }
    }
}