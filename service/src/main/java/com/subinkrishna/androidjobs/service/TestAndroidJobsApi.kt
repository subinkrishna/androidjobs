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