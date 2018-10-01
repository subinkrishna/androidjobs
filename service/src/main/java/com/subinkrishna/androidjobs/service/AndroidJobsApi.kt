package com.subinkrishna.androidjobs.service

import com.subinkrishna.androidjobs.service.model.JobListing
import io.reactivex.Single

interface AndroidJobsApi {
    fun getJobs(): Single<List<JobListing>>
}