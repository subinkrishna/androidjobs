package com.subinkrishna.androidjobs.service.converter

import com.subinkrishna.androidjobs.service.model.JobListing
import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * A jsoup based AndroidJobs.io job listing response [Converter] implementation
 *
 * @author Subinkrishna Gopi
 * @see Converter.Factory
 * @see JobListing
 */
class JobListingJsoupConverter : Converter<ResponseBody, List<JobListing>> {
    override fun convert(value: ResponseBody): List<JobListing> = JobListingParser.parse(value.string())
}