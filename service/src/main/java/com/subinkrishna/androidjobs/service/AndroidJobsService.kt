package com.subinkrishna.androidjobs.service

import com.subinkrishna.androidjobs.service.model.JobListing
import io.reactivex.Single
import retrofit2.http.GET

interface  AndroidJobsService {
    @GET(RetrofitAndroidJobsApi.BASE_URL)
    fun getJobs(): Single<List<JobListing>>
}