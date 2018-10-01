package com.subinkrishna.androidjobs.service.converter

import com.subinkrishna.androidjobs.service.model.JobListing
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Factory for [JobListingJsoupConverter]
 *
 * @author Subinkrishna Gopi
 */
class JobListingJsoupConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<ResponseBody, List<JobListing>>? {
        return JobListingJsoupConverter()
    }
}