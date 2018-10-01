package com.subinkrishna.androidjobs.service

import com.subinkrishna.androidjobs.service.converter.JobListingJsoupConverterFactory
import com.subinkrishna.androidjobs.service.model.JobListing
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 *
 */
class RetrofitAndroidJobsApi : AndroidJobsApi {

    companion object {

        // Base URL
        internal const val BASE_URL = "https://androidjobs.io"

        private val httpClientBuilder by lazy {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            OkHttpClient.Builder().apply {
                addInterceptor(loggingInterceptor)
                // addInterceptor() // cache interceptor
                // cache()
            }
        }

        private val retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JobListingJsoupConverterFactory())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClientBuilder.build())
                    .build()
        }

        private val androidJobsService by lazy { retrofit.create(AndroidJobsService::class.java) }
    }

    override fun getJobs(): Single<List<JobListing>> {
        return androidJobsService.getJobs()
    }
}