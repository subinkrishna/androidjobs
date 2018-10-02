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