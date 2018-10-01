package com.subinkrishna.androidjobs.ui.listing

import com.subinkrishna.androidjobs.service.model.JobListing

/**
 * LCE View state
 *
 * @author Subinkrishna Gopi
 */
data class JobListingViewState(val isLoading: Boolean = false,
                               val content: List<JobListing>? = null,
                               val error: Any? = null)