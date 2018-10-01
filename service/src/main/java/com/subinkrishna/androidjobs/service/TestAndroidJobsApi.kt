package com.subinkrishna.androidjobs.service

import com.subinkrishna.androidjobs.service.converter.JobListingParser
import com.subinkrishna.androidjobs.service.model.JobListing
import io.reactivex.Single
import org.jsoup.Jsoup
import java.io.File

/**
 * Test API implementation that uses a local file to extract the [JobListing]s
 *
 * @author Subinkrishna Gopi
 */
internal class TestAndroidJobsApi : AndroidJobsApi {
    override fun getJobs(): Single<List<JobListing>> {
        return Single.fromCallable {
            JobListingParser.parse(Jsoup.parse(File("./html/sample.html"), "UTF-8"))
        }
    }
}