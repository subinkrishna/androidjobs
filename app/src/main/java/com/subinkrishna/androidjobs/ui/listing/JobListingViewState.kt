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
package com.subinkrishna.androidjobs.ui.listing

import com.subinkrishna.androidjobs.service.model.JobListing

/**
 * View state, events & results.
 *
 * @author Subinkrishna Gopi
 */
data class JobListingViewState(
        val isLoading: Boolean = false,
        val content: List<JobListing>? = null,
        val error: Any? = null,
        val itemInFocus: JobListing? = null
) {
    override fun toString(): String {
        return "JobListingViewState(isLoading: $isLoading, content: ${content?.size ?: 0}, error: $error, itemInFocus: $itemInFocus)"
    }
}

sealed class JobListingEvent {
    object FetchJobsEvent : JobListingEvent()
    data class ItemSelectEvent(val item: JobListing?) : JobListingEvent()
}

sealed class JobListingResult {
    data class FetchJobsResult(
            val items: List<JobListing>? = null,
            val error: Throwable? = null
    ) : JobListingResult()
    data class ItemSelectResult(val item: JobListing?) : JobListingResult()
}
