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
package com.subinkrishna.androidjobs.service

import com.subinkrishna.androidjobs.service.converter.JobListingJsoupConverterFactory
import com.subinkrishna.androidjobs.service.model.JobListing
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

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